// Prometheus 질의 공용. local은 :9090 직접, prod는 Grafana 데이터소스 프록시 경유.
const GRAFANA = process.env.GRAFANA_URL ?? 'https://monitoring.allgomath.com'
const PROM = process.env.PROM_URL ?? 'http://localhost:9090'

export function makeClient(mode) {
  if (mode === 'prod') {
    const token = process.env.GRAFANA_TOKEN
    if (!token) throw new Error('GRAFANA_TOKEN 환경변수가 필요합니다 (Viewer 권한 서비스 계정 토큰)')
    return {
      mode,
      label: `${GRAFANA} (datasource proxy)`,
      base: `${GRAFANA}/api/datasources/proxy/uid/prometheus/api/v1`,
      headers: { Authorization: `Bearer ${token}` },
    }
  }
  return { mode: 'local', label: PROM, base: `${PROM}/api/v1`, headers: {} }
}

/** instant query. 실패해도 throw하지 않고 error를 담아 돌려준다. */
export async function query(client, expr) {
  const url = `${client.base}/query?query=${encodeURIComponent(expr)}`
  try {
    const res = await fetch(url, { headers: client.headers })
    const body = await res.json()
    if (!res.ok || body.status !== 'success') {
      return { ok: false, error: body.error ?? `HTTP ${res.status}`, raw: body }
    }
    return { ok: true, result: body.data.result, raw: body }
  } catch (e) {
    return { ok: false, error: e.message, raw: null }
  }
}

/** 스칼라 1개를 기대하는 질의. 값이 없으면 null. */
export async function scalar(client, expr) {
  const r = await query(client, expr)
  if (!r.ok || !r.result?.length) return { ...r, value: null }
  return { ...r, value: Number(r.result[0].value[1]) }
}

/** 시리즈를 {labelValue: number} 로 접는다. */
export async function seriesBy(client, expr, label) {
  const r = await query(client, expr)
  if (!r.ok) return { ...r, map: {} }
  const map = {}
  for (const s of r.result ?? []) map[s.metric[label] ?? '(none)'] = Number(s.value[1])
  return { ...r, map }
}
