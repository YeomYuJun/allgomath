#!/usr/bin/env node
// 전 POST 엔드포인트 스모크 테스트 (Node 18+).
// 사용법: 서버 기동 후  node scripts/smoke-post.mjs [baseUrl]   (기본 http://localhost:8080)
// 각 요청이 200 + JSON 본문을 반환하면 PASS. 하나라도 실패하면 exit 1.
const BASE = process.argv[2] || 'http://localhost:8080'

const CASES = [
  { name: 'fourier', path: '/api/algorithms/fourier/series',
    payload: { wave: 'square', N: 5 } },
  { name: 'bezier', path: '/api/algorithms/bezier/compute',
    payload: { controlPoints: [[0, 0], [0.5, 1], [1, 0]], samples: 20 } },
  { name: 'sort', path: '/api/algorithms/sort/run',
    payload: { algorithm: 'bubble', values: [5, 3, 8, 1, 9, 2] } },
  { name: 'bfs', path: '/api/algorithms/bfs/search',
    payload: { rows: 4, cols: 4, walls: Array(16).fill(false), start: 0, goal: 15, diag: false } },
  { name: 'dfs', path: '/api/algorithms/dfs/search',
    payload: { rows: 4, cols: 4, walls: Array(16).fill(false), start: 0, goal: 15 } },
  { name: 'voronoi', path: '/api/algorithms/voronoi/compute',
    payload: { sites: [[0.2, 0.3], [0.7, 0.8], [0.5, 0.1]], metric: 'euclid', grid: 16 } },
  { name: 'greedy', path: '/api/algorithms/greedy/schedule',
    payload: { tasks: [{ s: 0, e: 3 }, { s: 2, e: 5 }, { s: 4, e: 7 }], strategy: 'finish' } },
  { name: 'automata', path: '/api/algorithms/automata/life/simulate',
    payload: { grid: [[false, true, false], [false, true, false], [false, true, false]], steps: 3 } },
  { name: 'plotter-surface', path: '/api/algorithms/plotter/surface',
    payload: { fn: 'bowl', range: 4, resolution: 16 } },
  { name: 'plotter-gradient-descent', path: '/api/algorithms/plotter/gradient-descent',
    payload: { fn: 'bowl', startX: 3, startY: 3, learningRate: 0.1, maxIterations: 100 } },
  { name: 'dp', path: '/api/algorithms/dp/solve',
    payload: { grid: [[1, 3, 2], [4, 2, 1], [1, 5, 3]], mode: 'max' } },
  { name: 'pendulum', path: '/api/algorithms/pendulum/simulate',
    payload: { state: { a: { t1: 2.0, t2: 2.0, w1: 0, w2: 0 }, b: { t1: 2.01, t2: 2.0, w1: 0, w2: 0 } },
      gravity: 1.0, armRatio: 1.0, damping: 0.0, steps: 20 } },
  { name: 'flow', path: '/api/algorithms/flow/simulate',
    payload: { particles: [[10, 10], [20, 20]], scale: 1.0, time: 0, steps: 5 } },
  { name: 'lissajous', path: '/api/algorithms/lissajous/simulate',
    payload: { a: 3, b: 2, delta: 1.57, phase: 0, steps: 100 } },
  { name: 'monte-carlo', path: '/api/monte-carlo/integrate',
    payload: { iterations: 200, bounds: { xMin: -2, xMax: 2, yMin: -2, yMax: 2 }, functionType: 'circle' } },
  { name: 'fourier-transform', path: '/api/algorithms/fourier/transform',
    payload: { signal: [0, 1, 0, -1, 0, 1, 0, -1, 0, 1, 0, -1, 0, 1, 0, -1], sampleRate: 16 } },
]

let failed = 0
for (const c of CASES) {
  try {
    const res = await fetch(BASE + c.path, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(c.payload),
    })
    let body = null
    try { body = await res.json() } catch { /* non-JSON */ }
    if (res.ok && body !== null) {
      console.log(`PASS ${c.name} (${res.status})`)
    } else {
      failed++
      console.log(`FAIL ${c.name} (${res.status}) ${body ? JSON.stringify(body).slice(0, 200) : 'non-JSON body'}`)
    }
  } catch (e) {
    failed++
    console.log(`FAIL ${c.name} - ${e.message}`)
  }
}
console.log(failed ? `${failed}/${CASES.length} FAILED` : `ALL ${CASES.length} PASSED`)
process.exit(failed ? 1 : 0)
