# AllgoMath

수학적 알고리즘과 시각화를 위한 교육용 플랫폼. 3D 수학 함수, 경사하강법, FFT 변환, 프랙탈 구조, 몬테 카를로 적분, 베지어 곡선 등을 실시간으로 시각화함.

**Backend**: Spring Boot 3.4.3 + Java 17 | **Frontend**: Vue 3 + Three.js | **Cache**: Redis + Spring Cache

> 수학과 알고리즘을 직관적으로 이해할 수 있는 웹 기반 시각화 도구를 목표로 개발 진행 중
> 

---

## 📐 주요 기능

### 3D Function Plotter

사용자 정의 수학 함수 `z = f(x,y)`를 3차원 메시로 실시간 렌더링하여 함수의 형태를 직관적으로 탐색

볼록함수에 대한 경사하강법 최적화 과정을 3D 공간에서 시각화하고 수렴 과정을 단계별로 추적 기능 제공

### Fourier Transform (FFT)

시간 도메인 신호를 주파수 도메인으로 변환하고 Winding Visualization을 통해 FFT 원리를 교육적으로 설명

### Fractal Generator

Mandelbrot, Julia 와 같은 다양한 프랙탈을 동적으로 생성하고 매개변수 조작을 통한 변화 관찰

### Monte Carlo Integration

무작위 샘플링을 통한 수치 적분 과정을 시각화하고 샘플 수에 따른 정확도 수렴을 실시간으로 분석

### Bezier Curves

n차 베지어 곡선의 생성, 미분, 차수 상승, 곡선 길이 계산 등 파라메트릭 곡선의 다양한 수학적 연산 제공

### Sorting Algorithms (개발 예정)

QuickSort, MergeSort, HeapSort 등 정렬 알고리즘의 단계별 실행 과정과 비교 연산 시각화 예정

### Laplace Transform (개발 예정)

시간 도메인 함수의 라플라스 변환 및 역변환 과정을 복소평면에서 시각화 예정

---

## 🏗️ 아키텍처

### 애플리케이션 구조

```
┌─────────────────────────────────────────────────────────────┐
│                        Client Layer                         │
│  ┌──────────────────────────────────────────────────────┐   │
│  │   Vue 3 + Three.js (3D Rendering & Interaction)      │   │
│  │   https://github.com/YeomYuJun/front_allgo           │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                              ▼ REST API (JSON)
┌─────────────────────────────────────────────────────────────┐
│                   Spring Boot Backend                       │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────────────────────────────────────────┐        │
│  │          Controller Layer                       │        │
│  │  /api/math/functions, /api/fractal,             │        │
│  │  /api/fft, /api/bezier, /api/convex             │        │
│  └─────────────────────────────────────────────────┘        │
│                       ▼                                     │
│  ┌─────────────────────────────────────────────────┐        │
│  │          Service Layer                          │        │
│  │  FractalService, FFTService, BezierService,     │        │
│  │  MonteCarloService, SortService                 │        │
│  └─────────────────────────────────────────────────┘        │
│                       ▼                                     │
│  ┌──────────────────┐        ┌─────────────────────┐        │
│  │ Algorithm Layer  │        │   Cache Layer       │        │
│  │ FractalCalculator│◄───────│ TileCacheService    │        │
│  │ QuickSort, FFT   │        │ CachedFractalService│        │
│  └──────────────────┘        └─────────────────────┘        │
│                                     ▼                       │
│                         ┌─────────────────────┐             │
│                         │   Redis Cache       │             │
│                         │   (Computation)     │             │
│                         └─────────────────────┘             │
└─────────────────────────────────────────────────────────────┘
```

### 주요 설계 패턴

- **Layered Architecture**: Controller → Service → Algorithm 계층 분리로 관심사 명확화
- **Strategy Pattern**: 알고리즘별 독립 구현 (FractalCalculatorFactory, 정렬 알고리즘 클래스)
- **Cache-Aside Pattern**: Redis 기반 계산 결과 캐싱으로 중복 연산 최소화
- **DTO Pattern**: 클라이언트-서버 간 데이터 전송 객체 표준화

### 핵심 기술 스택

- **Framework**: Spring Boot 3.4.3, Spring Security, Spring Data JPA
- **Monitoring**: Spring Actuator,  Prometheus, Grafana
- **Build**: Gradle, Java 17

---

## 📊 모니터링 아키텍처

```
┌──────────────────────────────────────────────────────────────┐
│                   Monitoring Stack                           │
├──────────────────────────────────────────────────────────────┤
│  ┌──────────────┐     ┌──────────────┐     ┌───────────────┐ │
│  │  Spring Boot │───▶│  Prometheus  │───▶│   Grafana     │ │
│  │   Actuator   │     │   :9090      │     │    :3001      │ │
│  │  /actuator/* │     │  (Metrics)   │     │(Visualization)│ │
│  └──────────────┘     └──────────────┘     └───────────────┘ │
│                              ▲                               │
│  ┌──────────────┐     ┌───────────────┐                      │
│  │    Redis     │───▶│Redis Exporter │                      │
│  │   :6379      │     │   :9121       │                      │
│  │  (Cache)     │     │ (Cache Metrics)                      │
│  └──────────────┘     └───────────────┘                      │
└──────────────────────────────────────────────────────────────┘
```

### Docker Compose 구성

- **allgomath-api**: Spring Boot 애플리케이션 (포트 8080)
- **redis**: Redis 캐시 서버 (포트 6379)
- **prometheus**: 메트릭 수집 및 저장 (포트 9090)
- **grafana**: 메트릭 시각화 대시보드 (포트 3001)
- **redis-exporter**: Redis 성능 지표 수집 (포트 9121)

---
프론트엔드는 별도 저장소에서 관리됨:

**Frontend Repository**: https://github.com/YeomYuJun/front_allgo
