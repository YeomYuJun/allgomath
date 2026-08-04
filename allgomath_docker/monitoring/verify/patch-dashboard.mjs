// allgomath_usage.json 패치. 재실행 안전.
// 1) 비컨이 API 사용량 패널을 오염시키지 않도록 /api/telemetry 제외
// 2) 사이트 방문(5행) 추가
// 3) 기존 결함 3건 수정
import fs from 'node:fs'
import path from 'node:path'
import { fileURLToPath } from 'node:url'

const HERE = path.dirname(fileURLToPath(import.meta.url))
const FILE = path.join(HERE, '../grafana/provisioning/dashboards/allgomath_usage.json')
const DS = { type: 'prometheus', uid: 'prometheus' }
const BROAD = 'uri=~"/api/.*"'
const FIXED = 'uri=~"/api/.*", uri!~"/api/telemetry.*"'

const dash = JSON.parse(fs.readFileSync(FILE, 'utf8'))
const changes = []

// 1) 광범위 셀렉터에 telemetry 제외 추가
for (const panel of dash.panels) {
  for (const t of panel.targets ?? []) {
    if (t.expr?.includes(BROAD) && !t.expr.includes('uri!~"/api/telemetry')) {
      t.expr = t.expr.split(BROAD).join(FIXED)
      changes.push(`제외필터 추가: panel ${panel.id} ${panel.title}`)
    }
  }
}

// 2) 기존 결함
const p17 = dash.panels.find((p) => p.id === 17)
if (p17 && !p17.targets[0].interval) {
  p17.targets[0].interval = '1h'
  changes.push('panel 17: interval 1h 지정')
}
const p5 = dash.panels.find((p) => p.id === 5)
if (p5 && !p5.targets[0].expr.includes('or vector(0)')) {
  p5.targets[0].expr = `${p5.targets[0].expr} or vector(0)`
  changes.push('panel 5: 유휴 시 No data 대신 0')
}
const p31 = dash.panels.findIndex((p) => p.id === 31)
if (p31 >= 0) {
  dash.panels.splice(p31, 1)
  changes.push('panel 31 제거: Fractal은 FE 렌더러로 재작성되어 영구 무데이터')
}

// 3) 사이트 방문 행
// 행 id는 38. 32는 기존 Tomcat 패널이 이미 쓰고 있고, 31은 위에서 지우는 id라 재실행 때 새 행이 삭제된다.
const stat = (id, title, expr, x, w) => ({
  id, type: 'stat', title, datasource: DS,
  gridPos: { x, y: 70, w, h: 4 },
  targets: [{ datasource: DS, editorMode: 'code', expr, legendFormat: title, range: false, instant: true, format: 'time_series', refId: 'A' }],
  fieldConfig: { defaults: { custom: {} }, overrides: [] },
  options: {
    colorMode: 'value', graphMode: 'area', justifyMode: 'auto', textMode: 'auto',
    reduceOptions: { calcs: ['lastNotNull'], fields: '', values: false },
  },
})

if (!dash.panels.some((p) => p.id === 33)) {
  dash.panels.push(
    { id: 38, type: 'row', title: '⑤ 사이트 방문 (PV / UV)', collapsed: false, gridPos: { x: 0, y: 69, w: 24, h: 1 }, panels: [] },
    stat(33, '오늘 PV', 'round(sum(increase(site_pageview_total[$__range]))) or vector(0)', 0, 5),
    stat(34, '오늘 UV', 'site_uv_today or vector(0)', 5, 5),
    stat(35, '거절된 비컨', 'round(sum(increase(site_pageview_rejected_total[$__range]))) or vector(0)', 10, 5),
    {
      id: 36, type: 'bargauge', title: '오늘 랩별 PV', datasource: DS,
      gridPos: { x: 15, y: 70, w: 9, h: 10 },
      targets: [{ datasource: DS, editorMode: 'code', expr: 'sort_desc(round(sum by (route) (increase(site_pageview_total[$__range]))))', legendFormat: '{{route}}', range: false, instant: true, format: 'time_series', refId: 'A' }],
      fieldConfig: { defaults: { custom: {} }, overrides: [] },
      options: { displayMode: 'gradient', orientation: 'horizontal', showUnfilled: true, reduceOptions: { calcs: ['lastNotNull'], fields: '', values: false } },
    },
    {
      id: 37, type: 'timeseries', title: 'PV 추이 (라우트별)', datasource: DS,
      gridPos: { x: 0, y: 74, w: 15, h: 6 },
      targets: [{ datasource: DS, editorMode: 'code', expr: 'sum by (route) (rate(site_pageview_total[5m]))', legendFormat: '{{route}}', range: true, instant: false, format: 'time_series', refId: 'A' }],
      fieldConfig: { defaults: { custom: {} }, overrides: [] },
      options: {
        legend: { displayMode: 'table', placement: 'bottom', showLegend: true, calcs: ['mean', 'max'] },
        tooltip: { mode: 'multi', sort: 'desc' },
      },
    },
  )
  changes.push('⑤ 사이트 방문 행 추가 (패널 33-38)')
}

dash.version = (dash.version ?? 1) + 1
fs.writeFileSync(FILE, `${JSON.stringify(dash, null, 2)}\n`, 'utf8')
console.log(changes.length ? changes.map((c) => `  ${c}`).join('\n') : '  변경 없음 (이미 패치됨)')
console.log(`\n패널 ${dash.panels.length}개, ${FILE}`)
