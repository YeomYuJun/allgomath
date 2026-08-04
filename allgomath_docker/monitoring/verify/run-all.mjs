// 시드 -> 스크레이프 대기 -> 대시보드 expr 실행 -> label_replace 감사 -> HTML 리포트.
// --prod 는 절대 시드하지 않는다. 프로덕션에 합성 트래픽을 쏘면 이 대시보드의 존재 이유인 실사용 수치가 영구 오염된다.
import { makeClient } from './lib/promql.mjs'
import { seed, warmup } from './seed-traffic.mjs'
import { verifyDashboard } from './verify-dashboard.mjs'
import { auditLabels } from './audit-labels.mjs'
import { renderReport } from './report.mjs'

// 스크레이프 간격이 15초라 35초면 사이에 최소 한 번은 확실히 긁힌다.
const SCRAPE_WAIT_MS = Number(process.env.SCRAPE_WAIT_MS ?? 35_000)

function parseArgs(argv) {
  const args = { prod: false, seedFlag: false, noSeed: false, noWarmup: false, range: '1h', strict: null }
  for (const a of argv) {
    if (a === '--prod') args.prod = true
    else if (a === '--seed') args.seedFlag = true
    else if (a === '--no-seed') args.noSeed = true
    else if (a === '--no-warmup') args.noWarmup = true
    else if (a === '--strict') args.strict = true
    else if (a === '--no-strict') args.strict = false
    else if (a.startsWith('--range=')) args.range = a.slice('--range='.length)
  }
  return args
}

function wait(ms, why) {
  console.log(`${why} ${ms / 1000}초 대기...`)
  return new Promise((r) => setTimeout(r, ms))
}

/** 한글은 터미널에서 두 칸을 차지한다. 표를 맞추려면 코드 단위가 아니라 표시 폭으로 세야 한다. */
function width(s) {
  let w = 0
  for (const ch of String(s)) w += /[ᄀ-ᅟ⺀-꓏가-힣豈-﫿︰-﹯＀-｠￠-￦]/.test(ch) ? 2 : 1
  return w
}

function pad(s, n) {
  const str = String(s ?? '')
  return str + ' '.repeat(Math.max(0, n - width(str)))
}

function clip(s, n) {
  let out = ''
  for (const ch of String(s ?? '')) {
    if (width(out) + width(ch) > n) return `${out}..`
    out += ch
  }
  return out
}

async function main() {
  const args = parseArgs(process.argv.slice(2))

  if (args.prod && args.seedFlag) {
    console.error('중단: --prod 와 --seed 는 같이 쓸 수 없습니다.')
    console.error('프로덕션에 합성 트래픽을 쏘면 이 대시보드가 존재하는 이유인 실사용 수치가 영구 오염됩니다.')
    process.exit(2)
  }

  const mode = args.prod ? 'prod' : 'local'
  const strict = args.strict === null ? mode === 'local' : args.strict
  const client = makeClient(mode)
  const startedAt = new Date().toISOString()

  const willSeed = mode === 'local' && !args.noSeed
  console.log(`모드 ${mode === 'prod' ? 'P (프로덕션, 시드 없음)' : `L (로컬, ${willSeed ? '시드 있음' : '시드 생략'})`}`)
  console.log(`대상 ${client.label}`)
  console.log(`range ${args.range} / strict ${strict}`)
  console.log('')

  let seedLog = []
  let seedOk = true
  if (willSeed) {
    if (!args.noWarmup) {
      console.log('워밍업 발사 중 (increase()의 기준선 샘플을 만든다)...')
      const warmLog = await warmup()
      for (const s of warmLog) if (!s.ok) console.log(`  BAD ${s.what} ${s.codes}`)
      seedLog = warmLog
      await wait(SCRAPE_WAIT_MS, '기준선 스크레이프')
    }
    console.log('시드 트래픽 발사 중...')
    seedLog = seedLog.concat(await seed())
    for (const s of seedLog) {
      console.log(`  ${s.ok ? 'OK ' : 'BAD'} ${pad(clip(s.what, 46), 46)} n=${pad(s.count, 4)} ${s.codes}`)
      if (!s.ok) seedOk = false
    }
    if (!seedOk) {
      console.error('')
      console.error('경고: 시드 응답 코드가 기대와 다릅니다. 숫자 대조 결과는 신뢰할 수 없습니다.')
      console.error('      body가 400이 나도 같은 uri 시리즈는 증가하므로 11/12는 맞고 13만 틀어져 대시보드 버그처럼 보입니다.')
    }
    console.log('')
    await wait(SCRAPE_WAIT_MS, 'Prometheus 스크레이프')
  } else if (mode === 'prod') {
    console.log('시드하지 않습니다. 프로덕션 실사용 수치를 오염시키지 않기 위함입니다.')
  }

  console.log('\n대시보드 expr 실행 중...')
  const rows = await verifyDashboard(client, { range: args.range, strict })

  console.log('')
  console.log(`${pad('패널', 6)}${pad('제목', 34)}${pad('ref', 5)}${pad('판정', 9)}상세`)
  console.log('-'.repeat(120))
  for (const r of rows) {
    console.log(
      `${pad(r.panelId, 6)}${pad(clip(r.title, 32), 34)}${pad(r.refId, 5)}${pad(r.verdict, 9)}${clip(r.detail, 60)}`
    )
  }

  console.log('\nlabel_replace 매핑 감사...')
  const audit = await auditLabels(client, args.range)
  console.log(`  관측 uri ${audit.observedUris.length}개 / 매핑 실패 ${audit.unmapped.length}개`)
  const inconclusive = audit.observedUris.length === 0
  if (inconclusive) {
    console.log('  감사 불가: 관측된 uri가 0개다. 아무것도 검증하지 못했으므로 통과로 보면 안 된다.')
  }
  for (const m of audit.mapping.filter((x) => !x.ok)) console.log(`  FAIL  ${m.uri} -> (매핑 실패)`)
  if (audit.hasNoneLabel) console.log('  FAIL  algo 라벨이 없는 시리즈가 있습니다. 조용한 오분류입니다.')
  for (const m of audit.algoMismatch) console.log(`  WARN  ${m}`)
  if (audit.queryError) console.log(`  WARN  ${audit.queryError}`)

  const out = renderReport({
    mode,
    target: client.label,
    range: args.range,
    seedLog,
    rows,
    audit,
    startedAt,
  })

  console.log('')
  console.log(`${out.pass} PASS / ${out.fail} FAIL / ${out.empty} EMPTY${out.missing ? ` / ${out.missing} MISSING` : ''}`)
  console.log(`리포트: ${out.file}`)

  const bad =
    out.fail > 0 || out.missing > 0 || audit.unmapped.length > 0 || audit.hasNoneLabel || inconclusive || !seedOk
  process.exit(bad ? 1 : 0)
}

main().catch((e) => {
  console.error(`실패: ${e.message}`)
  process.exit(2)
})
