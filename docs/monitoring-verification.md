# Grafana 대시보드 검증 런북

`allgomath_usage.json` 대시보드가 **실제로 맞는 숫자를 보여주는지** 확인하는 절차.

핵심 원칙은 하나다. 검증 스크립트는 **대시보드 JSON에서 `expr` 를 직접 뽑아 실행한다.**
손으로 다시 쓴 동등 쿼리가 맞다는 것은 패널이 맞다는 증거가 아니다.
그래서 리포트에는 실행된 PromQL 원문이 그대로 실린다. 스크립트를 믿지 않아도 Grafana Explore 에 복붙해 같은 숫자가 나오는지 직접 대조할 수 있다.

스크립트 위치: `allgomath_docker/monitoring/verify/`

---

## 1. 두 모드

| | **모드 L (로컬)** | **모드 P (프로덕션)** |
|---|---|---|
| 대상 | docker-compose 스택 Prometheus `:9090` 직접 | `monitoring.allgomath.com` Grafana 데이터소스 프록시 |
| 시드 | **한다** (합성 트래픽 발사) | **하지 않는다** |
| 증명하는 것 | **정확한 숫자.** 84 를 쏘고 84 가 나오는지 | **구조적 정합성.** 쿼리가 돌고 시리즈가 있고 라벨이 붙는지 |
| 놓치는 것 | 프로덕션 라벨/스크레이프 차이 | 표시된 숫자가 진짜인지 |
| strict 기본값 | on (스칼라 기대값 대조) | off (시리즈 유무만) |
| 인증 | 없음 | `GRAFANA_TOKEN` (Viewer 서비스 계정) |

프로덕션 Prometheus 는 nginx 가 Grafana 만 프록시하므로 직접 도달할 수 없다. 그래서 모드 P 는 Grafana 데이터소스 프록시를 경유한다.

**모드 P 에서 절대 시드하지 않는 이유:** 프로덕션에 합성 트래픽을 쏘면 이 대시보드가 존재하는 이유인 실사용 수치가 영구 오염된다. `--prod --seed` 를 같이 주면 스크립트가 하드 에러로 종료한다.

---

## 2. 실행

```bash
cd allgomath_docker/monitoring/verify

# 모드 L: 로컬 스택에 시드하고 정확한 숫자를 대조한다
node run-all.mjs
node run-all.mjs --range=6h        # range 변경 (기본 1h)
node run-all.mjs --no-seed         # 이미 시드했고 다시 쏘고 싶지 않을 때

# 모드 P: 프로덕션 구조 점검 (시드 없음)
GRAFANA_TOKEN=glsa_xxx node run-all.mjs --prod

# 시드만 따로
node seed-traffic.mjs
```

환경변수: `API_URL`(기본 `http://localhost:8080`), `PROM_URL`(기본 `http://localhost:9090`), `GRAFANA_URL`(기본 `https://monitoring.allgomath.com`), `GRAFANA_TOKEN`.

리포트는 `verify/out/report.html` 에 생성된다. FAIL 이 있거나 라벨 매핑에 실패한 uri 가 있으면 종료 코드 1.

### 실행 순서와 워밍업 (중요)

```
워밍업 발사 -> 35초 대기 -> 본 시드 발사 -> 35초 대기 -> 질의
```

`increase(X[1h])` 는 **구간 안의 (마지막 샘플 - 첫 샘플)** 이다. 그런데 Micrometer 의 카운터 시리즈는 그 uri 로 첫 요청이 들어와야 비로소 생긴다. 그래서 아무것도 없는 스택에 61발을 2초 안에 몰아 쏘면, 그 시리즈의 **첫 스크레이프가 이미 61** 이고 이후는 평평하다. 증가분은 **0** 이 된다. 대시보드가 멀쩡한데도 패널 11/12/13/33 이 전부 0 으로 읽혀 FAIL 벽이 만들어진다.

그래서 본 시드 전에 엔드포인트마다 1발씩(비컨은 route 마다 1발씩) 워밍업을 쏘고 스크레이프를 한 번 받는다. 그 값이 기준선이 되어 빠지므로 **기대값은 그대로 84 / 61 / 7 이다.**

대기는 각 35초다. 스크레이프 간격이 15초라 그 사이 최소 한 번은 확실히 긁힌다. `SCRAPE_WAIT_MS` 로 조절할 수 있다.

이 증상인지 판별하는 질의:

```promql
count_over_time(site_pageview_total[1h])   # 1~2 가 나오고
increase(site_pageview_total[1h])          # 0 이 나오면 그것이다
```

`--no-warmup` 으로 건너뛸 수 있지만, 그러면 위 표의 기대값을 보장하지 못한다.

부차적으로 `increase()` 는 샘플이 걸친 시간이 구간보다 짧으면 그만큼 보정해서 부풀린다. 그래서 스칼라 대조는 정확히 같은 값이 아니라 **5% 또는 최소 1** 의 여유를 두고 판정하며, 리포트에 허용오차를 함께 찍는다.

### 재실행 시 주의

- **PV(`site_pageview_total`) 는 누적된다.** 두 번 시드하면 61 이 아니라 122 다. range 창 하나당 시드는 한 번만.
- **UV 는 재시드해도 7 로 유지된다.** cid 가 고정 UUID(`00000000-0000-4000-8000-0000000000NN`)라 HyperLogLog 가 같은 값을 다시 세지 않는다. 다른 날짜로 넘어가면 키(`uv:yyyyMMdd`, Asia/Seoul)가 바뀌어 0 부터 다시 센다.
- **UV 는 Redis 가 있어야 올라간다.** Redis 가 죽으면 PV 는 정상이고 UV 만 0 또는 -1 이 된다.

---

## 3. 시드가 쏘는 것

`expectations.json` 에 정의한다. 엔드포인트마다 **서로 다른 소수**를 쏘는 이유는, 모두 10회씩이면 bezier 와 voronoi 라벨이 뒤바뀌어도 알 수 없기 때문이다.

| 대상 | 경로 | 횟수 | 기대 응답 |
|---|---|---|---|
| bezier | `POST /api/algorithms/bezier/compute` | 3 | 200 |
| voronoi | `POST /api/algorithms/voronoi/compute` | 5 | 200 |
| bfs | `POST /api/algorithms/bfs/search` | 7 | 200 |
| dfs | `POST /api/algorithms/dfs/search` | 11 | 200 |
| dp | `POST /api/algorithms/dp/solve` | 13 | 200 |
| greedy | `POST /api/algorithms/greedy/schedule` | 17 | 200 |
| sort | `POST /api/algorithms/sort/run` | 19 | 200 |
| errors | `POST /api/algorithms/sort/run` (빈 body) | 9 | **400** |
| beacons | `POST /api/telemetry/pv` | 61 (cid 7종) | 204 |

알고리즘 합계 = 3+5+7+11+13+17+19 = **75**. 에러 9 를 더해 **84**.

**응답 코드가 기대와 다르면 숫자 대조는 무의미하다.** body 가 400 이 나도 같은 `uri` 시리즈는 증가하므로 패널 11/12 는 맞고 13 만 틀어져 대시보드 버그처럼 보인다. `run-all.mjs` 는 이 경우 크게 경고하고 종료 코드 1 을 낸다.

---

## 4. Grafana 눈검사표 (시간범위 `Today so far`)

**전제:** 워밍업을 거친 `node run-all.mjs` 직후이고, 그날 다른 트래픽이 없다. 워밍업을 건너뛰었다면 아래 값은 보장되지 않는다(§2 참고). 브라우저에서 대시보드를 열고 아래 값과 대조한다.

| 패널 | 기대값 | 틀렸다면 |
|---|---|---|
| ② 오늘 총 API 호출 | **84** | 아래 비컨 항목 참고 |
| ② 오늘 알고리즘 API 호출 | **84** | 총 호출과 같아야 정상 |
| ② 오늘 에러 수 (4xx+5xx) | **9** | 시드 응답 코드부터 확인 |
| ② 오늘 에러율 | **약 10.7%** (9/84) | |
| ③ 알고리즘별 막대 | bezier 3 / voronoi 5 / bfs 7 / dfs 11 / dp 13 / greedy 17 / **sort 28** | 숫자가 서로 뒤바뀌었으면 라벨 오분류 |
| ⑤ 오늘 PV | **61** | |
| ⑤ 오늘 UV | **7** | 0 이면 Redis 확인 |
| ⑤ 거절된 비컨 | **0 또는 "No data"** | 0 이 아니면 route 오타 |

주의할 점 세 가지.

1. **② 총 호출에 비컨 61 이 섞이면 안 된다.** 84 가 아니라 145 가 보이면 `uri` 필터에서 `/api/telemetry` 제외가 빠진 것이다. 초과분이 정확히 61 이라는 게 결정적 단서다. 비컨은 페이지뷰지 API 호출이 아니다. 섞이면 "오늘 API 호출" 이 방문자 수에 따라 부풀어 지표로서 의미를 잃는다.

2. **③ 의 sort 는 19 가 아니라 28 이다.** 의도적 400 을 `/api/algorithms/sort/run` 에 쏘는데, Spring 은 핸들러를 매핑한 뒤에 `@Valid` 를 돌리므로 실패 요청도 `uri` 라벨이 `/api/algorithms/sort/run` 으로 찍힌다. 그래서 19 + 9 = 28. 같은 이유로 ② 총 호출과 ② 알고리즘 호출이 둘 다 84 로 **같아야** 한다. **두 값이 다르면** 비컨이 총 호출에 샜거나 `/api/` 아래에 알고리즘 필터 밖의 새 엔드포인트가 생긴 것이다.

3. **⑤ 거절된 비컨의 "No data" 는 정상이다.** `site.pageview.rejected` 카운터는 거절이 실제로 일어날 때 지연 등록된다. 시드가 쓰는 route 는 전부 `KnownRoutes` 에 있으므로 카운터가 아예 만들어지지 않고, 메트릭 자체가 노출되지 않는다. 패널 expr 에 `or vector(0)` 이 있으면 0 으로 보이고, 없으면 "No data" 로 보인다. 둘 다 통과 상태다.

---

## 5. Query Inspector 로 직접 대조하기

표시된 숫자와 실제 응답 JSON 을 맞춰보는 방법.

1. 패널 제목을 클릭하거나 우클릭 -> **Inspect** -> **Query**
2. **Refresh** 를 눌러 질의를 다시 보낸다
3. `Query` 탭에서 Grafana 가 보낸 요청의 `query` 필드를 확인한다. 이게 대시보드 JSON 의 `expr` 에 변수가 치환된 실제 문자열이다
4. `Response` 탭의 `data.result[].value[1]` 이 패널에 찍힌 숫자와 같은지 본다
5. 같은 `query` 문자열을 **Explore** 에 붙여넣어 재현한다

리포트 HTML 의 각 행 아래 `pre` 블록에 있는 PromQL 이 바로 4번에서 보게 될 문자열이다. 스크립트를 신뢰하지 않아도 이 경로로 직접 대조할 수 있다.

패널이 값을 보여주는데 Response 가 비어 있거나, `algo` 같은 라벨이 없는 시리즈가 섞여 있으면 그 패널은 조용히 잘못 분류되고 있는 것이다.

---

## 6. label_replace 매핑 감사

패널 20/21/24 의 중첩 `label_replace` 5단이 이 대시보드에서 가장 깨지기 쉽다.

**Prometheus 의 `label_replace` 는 정규식이 `uri` 값 전체와 일치해야 라벨을 붙인다.** 일치하지 않으면 에러가 나지 않고 시리즈가 `algo` 라벨 없이 그냥 통과한다. 결과적으로 조용히 잘못 분류되는데 **그래프는 정상으로 보인다.**

`audit-labels.mjs` 는 실제 관측된 `uri` 를 열거한 뒤, 패널 20 의 체인과 동일한 규칙(뒤 규칙이 앞 규칙을 덮어씀)을 JS 정규식으로 앵커를 붙여 재현하고, 그 결과를 실제 시리즈의 `algo` 라벨과 대조한다.

| 순서 | 패턴 | 결과 |
|---|---|---|
| 1 | `^/api/algorithms/([^/]+)/.*$` | `$1` |
| 2 | `^/api/monte-carlo/.*$` | `montecarlo` |
| 3 | `^/api/fractal/.*$` | `fractal` |
| 4 | `^/api/algorithms/fourier/series$` | `fourier` |
| 5 | `^/api/algorithms/fourier/transform$` | `fourier-transform` |

리포트의 매핑 표에서 빨간 행은 그 `uri` 가 어느 규칙에도 걸리지 않았다는 뜻이다. 규칙 1 은 `/api/algorithms/<algo>/<something>` 처럼 **세 마디 이상**이어야 걸린다. 두 마디짜리 새 엔드포인트를 추가하면 조용히 누락된다.

`(none)` 이라는 algo 가 시리즈에 나타나면 그것이 오분류의 직접 증거다.

---

## 7. 스크립트 없이 되는 확인

메트릭이 애초에 노출되고 있는지만 보고 싶을 때.

```bash
curl -s http://localhost:9099/actuator/prometheus | grep '^site_'
```

깨끗한 시드 직후라면 `site_pageview_total{route=...}` 여러 줄과 `site_uv_today` 한 줄이 나온다. `site_pageview_rejected_total` 은 거절이 없었다면 **나오지 않는 것이 정상**이다.

**액추에이터는 9099 에 있다. 8080 의 잘못된 경로는 CloudFront 가 200 HTML 로 가리므로 브라우저 네트워크 탭의 200 은 증거가 아니다. 이 grep 이 증거다.**

Prometheus 가 그걸 실제로 긁어갔는지까지 보려면:

```bash
curl -s 'http://localhost:9090/api/v1/query?query=up{job="allgomath-api"}'
curl -s --get 'http://localhost:9090/api/v1/query' --data-urlencode 'query=sum(site_pageview_total)'
```

---

## 8. 판정 값

| 판정 | 뜻 |
|---|---|
| `PASS` | 스칼라 기대값과 일치하거나, 시리즈가 1개 이상 반환됨 |
| `FAIL` | 쿼리 오류이거나, 스칼라 기대값과 불일치 |
| `EMPTY` | 쿼리는 성공했으나 시리즈 0개이거나, `or vector()` 폴백만 돌아왔다 |
| `MISSING` | 기대한 패널 id 가 대시보드 JSON 에 아예 없음 |

`EMPTY` 는 로컬에서 Redis 나 JVM 패널에 흔히 뜬다. 그 자체로는 대시보드 버그가 아닐 수 있으니 어떤 패널이 비었는지 보고 판단한다.

**⑤ 의 패널 33/34/35 는 expr 끝에 `or vector(0)` 이 붙어 있다.** 그래서 메트릭이 아예 없어도 시리즈가 하나 돌아온다. 시리즈 유무만 보는 모드 P 에서는 이 세 패널이 무조건 통과해버려 아무것도 증명하지 못한다. 그래서 검증기는 라벨이 하나도 없는 시리즈 1개만 돌아오면 폴백으로 간주해 `EMPTY` 로 판정한다.

모드 P 에서 관측된 uri 가 0개이면 감사가 성립하지 않은 것이므로 통과로 처리하지 않고 종료 코드 1 을 낸다.

---

## 9. 파일

| 파일 | 역할 |
|---|---|
| `verify/lib/promql.mjs` | Prometheus 질의 공용. local 은 :9090 직접, prod 는 Grafana 프록시 |
| `verify/expectations.json` | 시드 횟수와 실제 컨트롤러 경로/body |
| `verify/seed-traffic.mjs` | 합성 트래픽 발사 |
| `verify/verify-dashboard.mjs` | 대시보드 JSON 의 expr 수집 및 실행 |
| `verify/audit-labels.mjs` | `label_replace` 체인 재현 및 대조 |
| `verify/report.mjs` | 자립형 HTML 리포트 |
| `verify/run-all.mjs` | 전체 오케스트레이션 |
