// 패널 20/21/24의 중첩 label_replace 5단이 이 대시보드에서 가장 깨지기 쉽다.
// Prometheus label_replace는 전체 일치를 요구하므로, 정규식이 uri 전체를 덮지 못하면
// 시리즈가 algo 라벨 없이 통과해 조용히 잘못 분류된다. 그래프는 정상으로 보인다.
import { query, seriesBy } from './lib/promql.mjs'
import { loadDashboard, collectTargets, substituteVars } from './verify-dashboard.mjs'

const URI_FILTER = '/api/(algorithms/.*|fractal/.*|monte-carlo/.*)'
export const ENUMERATE_URIS =
  `count by (uri) (http_server_requests_seconds_count{job="allgomath-api", uri=~"${URI_FILTER}"})`

/** 패널 20의 label_replace 체인과 같은 규칙. 앞뒤 앵커는 label_replace의 전체 일치를 재현한다. */
export const LABEL_RULES = [
  { pattern: '/api/algorithms/([^/]+)/.*', re: /^\/api\/algorithms\/([^/]+)\/.*$/, algo: (m) => m[1] },
  { pattern: '/api/monte-carlo/.*', re: /^\/api\/monte-carlo\/.*$/, algo: () => 'montecarlo' },
  { pattern: '/api/fractal/.*', re: /^\/api\/fractal\/.*$/, algo: () => 'fractal' },
  { pattern: '/api/algorithms/fourier/series', re: /^\/api\/algorithms\/fourier\/series$/, algo: () => 'fourier' },
  { pattern: '/api/algorithms/fourier/transform', re: /^\/api\/algorithms\/fourier\/transform$/, algo: () => 'fourier-transform' },
]

/** 뒤 규칙이 앞 규칙을 덮어쓴다. label_replace 체인 순서와 동일하다. */
export function mapUri(uri) {
  let algo = null
  let rule = null
  for (const r of LABEL_RULES) {
    const m = r.re.exec(uri)
    if (m) {
      algo = r.algo(m)
      rule = r.pattern
    }
  }
  return { algo, rule }
}

/** 패널 20의 expr을 대시보드에서 그대로 뽑는다. 없으면 null. */
function panel20Expr(range) {
  try {
    const t = collectTargets(loadDashboard(true)).find((x) => x.panelId === 20)
    return t ? substituteVars(t.expr, range) : null
  } catch {
    return null
  }
}

export async function auditLabels(client, range = '1h') {
  const enumerated = await query(client, ENUMERATE_URIS)
  const observedUris = enumerated.ok
    ? [...new Set((enumerated.result ?? []).map((s) => s.metric.uri).filter(Boolean))].sort()
    : []

  const mapping = observedUris.map((uri) => {
    const { algo, rule } = mapUri(uri)
    return { uri, algo, rule, ok: algo !== null }
  })
  const unmapped = mapping.filter((m) => !m.ok).map((m) => m.uri)

  const expr = panel20Expr(range)
  const series = expr ? await seriesBy(client, expr, 'algo') : { ok: false, error: '패널 20을 찾지 못했다', map: {} }
  const algoSeries = series.map ?? {}

  // 재현한 매핑이 만든 algo 집합과 실제 시리즈의 algo 라벨이 어긋나면 체인이 의도와 다르게 동작한 것이다.
  const expectedAlgos = new Set(mapping.filter((m) => m.ok).map((m) => m.algo))
  const actualAlgos = new Set(Object.keys(algoSeries))
  const algoMismatch = [
    ...[...actualAlgos].filter((a) => !expectedAlgos.has(a)).map((a) => `시리즈에만 있음: ${a}`),
    ...[...expectedAlgos].filter((a) => !actualAlgos.has(a)).map((a) => `매핑에만 있음: ${a}`),
  ]
  // (none)은 label_replace가 라벨을 못 붙인 시리즈다. 조용한 오분류의 직접 증거다.
  const hasNoneLabel = Object.prototype.hasOwnProperty.call(algoSeries, '(none)')

  return {
    observedUris,
    algoSeries,
    mapping,
    unmapped,
    algoMismatch,
    hasNoneLabel,
    panel20Expr: expr,
    enumerateExpr: ENUMERATE_URIS,
    queryError: enumerated.ok ? (series.ok === false ? series.error : null) : enumerated.error,
    ok: observedUris.length > 0 && unmapped.length === 0 && !hasNoneLabel && algoMismatch.length === 0,
  }
}
