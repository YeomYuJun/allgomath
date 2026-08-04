// 대시보드 JSON의 expr을 그대로 뽑아 실행한다. 손으로 다시 쓴 동등 쿼리는 패널이 맞다는 증거가 아니다.
import { readFileSync } from 'node:fs'
import { fileURLToPath } from 'node:url'
import { dirname, join } from 'node:path'
import { query } from './lib/promql.mjs'
import { loadExpectations } from './seed-traffic.mjs'

const HERE = dirname(fileURLToPath(import.meta.url))
export const DASHBOARD_PATH = join(HERE, '..', 'grafana', 'provisioning', 'dashboards', 'allgomath_usage.json')

// 지연 로드. 다른 에이전트가 JSON을 쓰는 중이어도 import 자체는 성공해야 한다.
let cached = null
export function loadDashboard(force = false) {
  if (!cached || force) cached = JSON.parse(readFileSync(DASHBOARD_PATH, 'utf8'))
  return cached
}

/** 접힌 row 아래에 중첩된 패널까지 훑는다. 평면 스캔만 하면 새 섹션을 통째로 놓친다. */
export function collectPanels(dashboard) {
  const out = []
  const walk = (list) => {
    for (const p of list ?? []) {
      if (p.type !== 'row') out.push(p)
      if (Array.isArray(p.panels)) walk(p.panels)
    }
  }
  walk(dashboard.panels)
  return out
}

export function collectTargets(dashboard) {
  const rows = []
  for (const p of collectPanels(dashboard)) {
    for (const t of p.targets ?? []) {
      if (typeof t.expr !== 'string' || t.expr.trim() === '') continue
      rows.push({ panelId: p.id, title: p.title ?? '(제목 없음)', refId: t.refId ?? '?', expr: t.expr })
    }
  }
  return rows
}

/** '90s' '30m' '6h' '2d' 를 초로. 못 읽으면 1시간. */
export function rangeSeconds(range) {
  const m = /^(\d+)(s|m|h|d|w)$/.exec(String(range).trim())
  if (!m) return 3600
  const unit = { s: 1, m: 60, h: 3600, d: 86400, w: 604800 }[m[2]]
  return Number(m[1]) * unit
}

/** Grafana 내장 변수 치환. _ms 접미 변수를 먼저 바꿔야 접두 일치로 깨지지 않는다. */
export function substituteVars(expr, range) {
  const secs = rangeSeconds(range)
  return expr
    .replaceAll('$__interval_ms', '60000')
    .replaceAll('$__range_ms', String(secs * 1000))
    .replaceAll('$__range_s', String(secs))
    .replaceAll('$__range', range)
    .replaceAll('$__rate_interval', '5m')
    .replaceAll('$__interval', '1m')
}

/**
 * 정확한 스칼라를 기대하는 패널. 값은 expectations.json에서 계산한다.
 * 에러는 /api/algorithms/sort/run 으로 나가므로 알고리즘 필터에도 잡힌다. 그래서 11과 12가 같아야 정상이다.
 */
export function derivedExpectations(exp = loadExpectations()) {
  const algoTotal = exp.endpoints.reduce((s, e) => s + e.count, 0)
  const errTotal = exp.errors.count
  const beacons = exp.beacons.count
  const algoWithErrors = algoTotal + (exp.errors.countsAsAlgorithm ? errTotal : 0)
  return {
    algoTotal,
    errTotal,
    beacons,
    panels: {
      // increase() 기반 패널은 구간 경계 보정 때문에 정수로 딱 떨어지지 않을 수 있어 여유를 준다.
      11: {
        value: algoTotal + errTotal,
        rate: true,
        note: `알고리즘 ${algoTotal} + 에러 ${errTotal}. 비컨 ${beacons}은 이 값에 섞이면 안 된다.`,
      },
      12: {
        value: algoWithErrors,
        rate: true,
        note: `알고리즘 ${algoTotal} + 에러 ${errTotal}(sort/run 이라 알고리즘 필터에도 잡힘). 11과 같아야 한다.`,
      },
      13: { value: errTotal, rate: true, note: `의도적 400 ${errTotal}건.` },
      33: { value: beacons, rate: true, note: `비컨 ${beacons}건 (site_pageview_total 합).` },
      // 게이지라 보정이 없다. 정확히 일치해야 한다.
      34: { value: exp.beacons.distinctCids, note: `고정 cid ${exp.beacons.distinctCids}개 (site_uv_today).` },
      35: {
        value: 0,
        nullOk: true,
        note: '거절 0. 카운터가 지연 등록이라 시리즈 자체가 없는 것이 정상 상태다.',
      },
    },
  }
}

/** increase() 패널은 5% 또는 최소 1의 여유를 준다. 게이지는 정확히 본다. */
function slackFor(expect) {
  return expect.rate ? Math.max(1, Math.ceil(0.05 * Math.abs(expect.value))) : 0.5
}

/** 폴백만 돌아온 시리즈. or vector(0)이 붙은 패널은 메트릭이 없어도 항상 값을 내므로 구분해야 한다. */
function isFallbackOnly(expr, series) {
  if (!/\bor\s+vector\s*\(/.test(expr)) return false
  return series.length === 1 && Object.keys(series[0].metric ?? {}).length === 0
}

/** 차이가 무엇 때문인지 바로 읽히게 한다. 초과분이 비컨 수와 같으면 제외 필터 누락이다. */
function diagnose(observed, expected, derived) {
  if (observed == null || expected == null) return ''
  const delta = observed - expected
  if (observed === 0 && expected > 0) {
    return ' 0이 나왔다. 카운터 시리즈가 구간 안에서 처음 생기면 increase()는 0을 낸다. 워밍업 후 재시드하라.'
  }
  if (delta === derived.beacons) {
    return ` 초과분이 ${delta}로 비컨 수와 정확히 같다. uri 필터에서 /api/telemetry 제외가 빠졌다.`
  }
  if (delta === derived.errTotal) {
    return ` 초과분이 ${delta}로 에러 수와 정확히 같다. 에러 제외 여부를 확인하라.`
  }
  if (Math.abs(delta) <= 2) {
    return ` 차이 ${delta}. increase()의 구간 경계 보정 오차일 수 있으니 다른 range로 재확인하라.`
  }
  return ''
}

export async function verifyDashboard(client, { range = '1h', strict = true } = {}) {
  const dashboard = loadDashboard(true)
  const targets = collectTargets(dashboard)
  const derived = derivedExpectations()
  const rows = []
  const seenPanels = new Set()

  for (const t of targets) {
    seenPanels.add(t.panelId)
    const expr = substituteVars(t.expr, range)
    const r = await query(client, expr)
    const expect = strict ? derived.panels[t.panelId] : undefined
    const base = { panelId: t.panelId, title: t.title, refId: t.refId, expr, raw: r.raw }

    if (!r.ok) {
      rows.push({ ...base, verdict: 'FAIL', detail: `쿼리 오류: ${r.error}` })
      continue
    }

    const series = r.result ?? []
    if (expect) {
      const value = series.length ? Number(series[0].value[1]) : null
      if (value === null) {
        const ok = expect.nullOk === true && expect.value === 0
        rows.push({
          ...base,
          verdict: ok ? 'PASS' : 'EMPTY',
          detail: `기대 ${expect.value} / 실제 시리즈 없음. ${expect.note}`,
        })
        continue
      }
      const slack = slackFor(expect)
      const ok = Math.abs(value - expect.value) <= slack
      const tol = expect.rate ? ` (허용오차 ${slack})` : ''
      rows.push({
        ...base,
        verdict: ok ? 'PASS' : 'FAIL',
        detail: `기대 ${expect.value}${tol} / 실제 ${value}. ${expect.note}${ok ? '' : diagnose(value, expect.value, derived)}`,
      })
      continue
    }

    if (isFallbackOnly(expr, series)) {
      rows.push({
        ...base,
        verdict: 'EMPTY',
        detail: 'or vector() 폴백만 반환됐다. 메트릭이 실제로 존재하지 않아 이 패널은 아무것도 증명하지 못한다.',
      })
      continue
    }

    rows.push({
      ...base,
      verdict: series.length ? 'PASS' : 'EMPTY',
      detail: series.length ? `시리즈 ${series.length}개` : '시리즈 0개',
    })
  }

  if (strict) {
    for (const [id, expect] of Object.entries(derived.panels)) {
      if (seenPanels.has(Number(id))) continue
      rows.push({
        panelId: Number(id),
        title: '(대시보드에 없음)',
        refId: '-',
        expr: '',
        verdict: 'MISSING',
        detail: `패널 ${id}이 대시보드 JSON에 없다. 기대 ${expect.value}. ${expect.note}`,
        raw: null,
      })
    }
  }

  rows.sort((a, b) => a.panelId - b.panelId || String(a.refId).localeCompare(String(b.refId)))
  return rows
}
