// 자립형 HTML 리포트. 외부 의존이 없어 파일 하나만 열면 된다.
// 각 행에 실행된 PromQL 원문을 그대로 실어, 사용자가 Grafana Explore에 복붙해 직접 대조할 수 있게 한다.
import { mkdirSync, writeFileSync } from 'node:fs'
import { fileURLToPath } from 'node:url'
import { dirname, join } from 'node:path'

const HERE = dirname(fileURLToPath(import.meta.url))

const COLOR = { PASS: '#7ee787', FAIL: '#ff7b72', EMPTY: '#d29922', MISSING: '#ff7b72' }

export function esc(v) {
  return String(v ?? '')
    .replaceAll('&', '&amp;')
    .replaceAll('<', '&lt;')
    .replaceAll('>', '&gt;')
    .replaceAll('"', '&quot;')
    .replaceAll("'", '&#39;')
}

function verdictSpan(v) {
  return `<span class="v" style="color:${COLOR[v] ?? '#e6e8ea'}">${esc(v)}</span>`
}

function seedSection(mode, seedLog) {
  if (mode === 'prod') {
    return `<p class="note">시드하지 않습니다. 프로덕션 실사용 수치를 오염시키지 않기 위함입니다.</p>`
  }
  const rows = (seedLog ?? [])
    .map((s) => {
      const ok = s.ok === undefined ? '' : s.ok ? 'OK' : 'BAD'
      const cls = s.ok === false ? ' class="bad"' : ''
      return `<tr${cls}><td>${esc(s.what)}</td><td class="num">${esc(s.count)}</td><td>${esc(s.codes)}</td><td>${esc(ok)}</td></tr>`
    })
    .join('\n')
  return `<table>
<thead><tr><th>대상</th><th>횟수</th><th>응답 코드</th><th>기대대로?</th></tr></thead>
<tbody>
${rows || '<tr><td colspan="4">시드 기록 없음</td></tr>'}
</tbody></table>`
}

function panelRows(rows) {
  return (rows ?? [])
    .map((r) => {
      const rawBlock =
        r.verdict === 'FAIL' && r.raw
          ? `<div class="rawlabel">원시 응답</div><pre class="raw">${esc(JSON.stringify(r.raw, null, 2))}</pre>`
          : ''
      const exprBlock = r.expr
        ? `<pre class="expr">${esc(r.expr)}</pre>`
        : `<pre class="expr">(대시보드에 이 패널이 없어 실행할 expr이 없습니다)</pre>`
      return `<tr class="head">
  <td class="num">${esc(r.panelId)}</td>
  <td>${esc(r.title)}</td>
  <td class="num">${esc(r.refId)}</td>
  <td>${verdictSpan(r.verdict)}</td>
  <td>${esc(r.detail)}</td>
</tr>
<tr class="body"><td colspan="5">${exprBlock}${rawBlock}</td></tr>`
    })
    .join('\n')
}

function auditSection(audit) {
  if (!audit) return '<p class="note">감사 결과 없음</p>'
  const mapRows = (audit.mapping ?? [])
    .map(
      (m) =>
        `<tr${m.ok ? '' : ' class="bad"'}><td><code>${esc(m.uri)}</code></td><td>${esc(m.algo ?? '(매핑 실패)')}</td><td><code>${esc(m.rule ?? '-')}</code></td><td>${m.ok ? 'OK' : 'FAIL'}</td></tr>`
    )
    .join('\n')
  const seriesRows = Object.entries(audit.algoSeries ?? {})
    .sort((a, b) => b[1] - a[1])
    .map(([k, v]) => `<tr><td>${esc(k)}</td><td class="num">${esc(v)}</td></tr>`)
    .join('\n')
  const warn = []
  if ((audit.unmapped ?? []).length) warn.push(`매핑 실패 uri: ${audit.unmapped.join(', ')}`)
  if (audit.hasNoneLabel) warn.push('algo 라벨이 붙지 않은 시리즈가 있습니다. 조용한 오분류입니다.')
  for (const m of audit.algoMismatch ?? []) warn.push(m)
  if (audit.queryError) warn.push(`쿼리 오류: ${audit.queryError}`)

  return `${warn.length ? `<p class="warn">${warn.map(esc).join('<br>')}</p>` : '<p class="okline">매핑 이상 없음</p>'}
<table>
<thead><tr><th>실제 uri</th><th>매핑된 algo</th><th>적용 규칙</th><th>판정</th></tr></thead>
<tbody>${mapRows || '<tr><td colspan="4">관측된 uri 없음</td></tr>'}</tbody>
</table>
<h3>패널 20이 실제로 돌려준 algo 시리즈</h3>
<table><thead><tr><th>algo</th><th>값</th></tr></thead>
<tbody>${seriesRows || '<tr><td colspan="2">시리즈 없음</td></tr>'}</tbody></table>
${audit.enumerateExpr ? `<div class="rawlabel">uri 열거에 쓴 PromQL</div><pre class="expr">${esc(audit.enumerateExpr)}</pre>` : ''}
${audit.panel20Expr ? `<div class="rawlabel">패널 20에서 그대로 뽑은 PromQL</div><pre class="expr">${esc(audit.panel20Expr)}</pre>` : ''}`
}

const CSS = `
:root{color-scheme:dark}
*{box-sizing:border-box}
body{margin:0;padding:32px;background:#0d0f11;color:#e6e8ea;
  font-family:ui-monospace,SFMono-Regular,Menlo,Consolas,"Noto Sans KR",monospace;font-size:14px;line-height:1.6}
h1{font-size:22px;margin:0 0 4px;color:#a3e635}
h2{font-size:17px;margin:36px 0 10px;color:#a3e635;border-bottom:1px solid #262b31;padding-bottom:6px}
h3{font-size:14px;margin:22px 0 8px;color:#a3e635}
.meta{color:#8b949e;font-size:12px;margin-bottom:20px}
.meta b{color:#e6e8ea}
.score{font-size:34px;font-weight:700;letter-spacing:1px;margin:18px 0 8px}
.score .p{color:#7ee787}.score .f{color:#ff7b72}.score .e{color:#d29922}.score .sep{color:#3a424b}
.note{background:#15191d;border-left:3px solid #a3e635;padding:10px 14px;margin:14px 0;color:#c9d1d9}
.warn{background:#241618;border-left:3px solid #ff7b72;padding:10px 14px;margin:14px 0;color:#ffb4ae}
.okline{color:#7ee787;margin:14px 0}
table{width:100%;border-collapse:collapse;margin:10px 0 18px;font-size:13px}
th{text-align:left;background:#15191d;color:#8b949e;font-weight:600;padding:8px 10px;border-bottom:1px solid #262b31}
td{padding:8px 10px;border-bottom:1px solid #1b2026;vertical-align:top}
tr.bad td{background:#1d1214}
tr.head td{border-bottom:none;padding-bottom:2px}
tr.body td{padding-top:0;padding-bottom:16px}
.num{text-align:right;white-space:nowrap}
.v{font-weight:700}
pre{margin:6px 0 0;padding:10px 12px;background:#111417;border:1px solid #21262d;border-radius:4px;
  white-space:pre-wrap;word-break:break-word;font-size:12px;color:#c9d1d9;overflow-x:auto}
pre.expr{border-left:3px solid #a3e635}
pre.raw{border-left:3px solid #ff7b72;max-height:280px;overflow:auto}
.rawlabel{color:#8b949e;font-size:11px;margin-top:8px}
code{color:#a3e635}
footer{margin-top:40px;color:#57606a;font-size:11px}
`

export function renderReport({ mode, target, range, seedLog, rows, audit, startedAt }) {
  const list = rows ?? []
  const pass = list.filter((r) => r.verdict === 'PASS').length
  const fail = list.filter((r) => r.verdict === 'FAIL').length
  const empty = list.filter((r) => r.verdict === 'EMPTY').length
  const missing = list.filter((r) => r.verdict === 'MISSING').length

  const html = `<title>Grafana 대시보드 검증 리포트</title>
<style>${CSS}</style>
<h1>Grafana 대시보드 검증 리포트</h1>
<div class="meta">
모드 <b>${esc(mode === 'prod' ? 'P (프로덕션 / 시드 없음)' : 'L (로컬 / 시드 있음)')}</b> ·
대상 <b>${esc(target)}</b> ·
range <b>${esc(range)}</b> ·
시작 <b>${esc(startedAt)}</b>
</div>

<div class="score">
<span class="p">${pass} PASS</span><span class="sep"> / </span><span class="f">${fail} FAIL</span><span class="sep"> / </span><span class="e">${empty} EMPTY</span>${missing ? `<span class="sep"> / </span><span class="f">${missing} MISSING</span>` : ''}
</div>

<p class="note">아래 PromQL은 대시보드 JSON에서 그대로 뽑은 것입니다. Grafana Explore에 붙여넣어 같은 값이 나오는지 직접 대조할 수 있습니다. 손으로 다시 쓴 동등 쿼리가 아니라 패널이 실제로 실행하는 문자열입니다.</p>

<h2>1. 시드 트래픽</h2>
${seedSection(mode, seedLog)}

<h2>2. 패널별 검증</h2>
<table>
<thead><tr><th>패널</th><th>제목</th><th>refId</th><th>판정</th><th>상세</th></tr></thead>
<tbody>
${panelRows(list) || '<tr><td colspan="5">검증 대상 없음</td></tr>'}
</tbody></table>

<h2>3. label_replace 매핑 감사</h2>
<p class="note">Prometheus label_replace는 정규식이 uri 값 전체와 일치해야 라벨을 붙입니다. 일치하지 않으면 시리즈가 algo 라벨 없이 통과해 조용히 잘못 분류되고, 그래프는 정상으로 보입니다.</p>
${auditSection(audit)}

<footer>allgomath monitoring verify · ${esc(startedAt)}</footer>
`

  const outDir = join(HERE, 'out')
  mkdirSync(outDir, { recursive: true })
  const file = join(outDir, 'report.html')
  writeFileSync(file, html, 'utf8')
  return { file, pass, fail, empty, missing }
}
