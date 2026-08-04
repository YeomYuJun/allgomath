// expectations.json 대로 로컬 API에 합성 트래픽을 쏜다. 로컬 전용이며 프로덕션에 쓰면 안 된다.
// 엔드포인트마다 개수를 다른 소수로 두어야 라벨이 뒤바뀌었을 때 숫자만 보고 잡을 수 있다.
import { readFileSync } from 'node:fs'
import { fileURLToPath, pathToFileURL } from 'node:url'
import { dirname, join } from 'node:path'

const HERE = dirname(fileURLToPath(import.meta.url))
const API = process.env.API_URL ?? 'http://localhost:8080'

export function loadExpectations() {
  return JSON.parse(readFileSync(join(HERE, 'expectations.json'), 'utf8'))
}

/** 고정 UUID. 같은 날 재시드해도 HyperLogLog UV가 늘어나지 않게 한다. */
export function cidFor(exp, index) {
  return exp.beacons.cidPrefix + String(index + 1).padStart(12, '0')
}

async function post(path, body) {
  try {
    const res = await fetch(`${API}${path}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body),
    })
    await res.text().catch(() => '')
    return res.status
  } catch (e) {
    return `ERR:${e.message}`
  }
}

/** [200,200,400] 을 "200 x2, 400 x1" 로 접는다. */
function tally(codes) {
  const counts = new Map()
  for (const c of codes) counts.set(String(c), (counts.get(String(c)) ?? 0) + 1)
  return [...counts.entries()].map(([c, n]) => `${c} x${n}`).join(', ')
}

function allAre(codes, expected) {
  return codes.length > 0 && codes.every((c) => c === expected)
}

/**
 * 워밍업. increase()는 구간 안의 (마지막 샘플 - 첫 샘플)이라, 카운터 시리즈가 구간 안에서
 * 처음 태어나면 첫 샘플이 이미 최종값이라 증가분이 0으로 읽힌다. 미리 1발씩 쏴서 기준선을 만든다.
 * 이 1발은 기준선으로 빠지므로 본 시드의 기대값은 그대로다.
 */
export async function warmup(exp = loadExpectations()) {
  const log = []
  for (const ep of exp.endpoints) {
    const code = await post(ep.path, ep.body)
    log.push({ what: `warmup ${ep.algo}`, count: 1, codes: tally([code]), expect: 200, ok: code === 200 })
  }
  const err = exp.errors
  const errCode = await post(err.path, err.body)
  log.push({
    what: 'warmup errors',
    count: 1,
    codes: tally([errCode]),
    expect: err.expectStatus ?? 400,
    ok: errCode === (err.expectStatus ?? 400),
  })

  // route별 시리즈가 각각 따로 태어나므로 route마다 1발씩. cid는 0번 하나만 써서 UV를 늘리지 않는다.
  const b = exp.beacons
  const codes = []
  for (const route of b.routes) codes.push(await post(b.path, { route, cid: cidFor(exp, 0) }))
  log.push({
    what: `warmup beacons (route ${b.routes.length}종)`,
    count: b.routes.length,
    codes: tally(codes),
    expect: b.expectStatus ?? 204,
    ok: allAre(codes, b.expectStatus ?? 204),
  })
  return log
}

export async function seed(exp = loadExpectations()) {
  const log = []

  for (const ep of exp.endpoints) {
    const codes = []
    for (let i = 0; i < ep.count; i += 1) codes.push(await post(ep.path, ep.body))
    log.push({
      what: `${ep.algo}  ${ep.method} ${ep.path}`,
      count: ep.count,
      codes: tally(codes),
      expect: 200,
      ok: allAre(codes, 200),
    })
  }

  // 의도적인 400. 빈 body라 @Valid가 거절한다. 여기가 2xx면 에러 패널 기대값이 무너진다.
  const err = exp.errors
  const errCodes = []
  for (let i = 0; i < err.count; i += 1) errCodes.push(await post(err.path, err.body))
  log.push({
    what: `errors(의도적 400)  ${err.method} ${err.path}`,
    count: err.count,
    codes: tally(errCodes),
    expect: err.expectStatus ?? 400,
    ok: allAre(errCodes, err.expectStatus ?? 400),
  })

  // 비컨. distinctCids개의 고정 cid와 KnownRoutes에 있는 route만 돌려가며 쏜다.
  const b = exp.beacons
  const beaconCodes = []
  for (let i = 0; i < b.count; i += 1) {
    const route = b.routes[i % b.routes.length]
    const cid = cidFor(exp, i % b.distinctCids)
    beaconCodes.push(await post(b.path, { route, cid }))
  }
  log.push({
    what: `beacons(${b.distinctCids} cid)  POST ${b.path}`,
    count: b.count,
    codes: tally(beaconCodes),
    expect: b.expectStatus ?? 204,
    ok: allAre(beaconCodes, b.expectStatus ?? 204),
  })

  return log
}

if (process.argv[1] && import.meta.url === pathToFileURL(process.argv[1]).href) {
  seed().then((log) => {
    for (const r of log) console.log(`${r.ok ? 'OK  ' : 'BAD '} ${r.what}  n=${r.count}  ${r.codes}`)
    process.exit(log.every((r) => r.ok) ? 0 : 1)
  })
}
