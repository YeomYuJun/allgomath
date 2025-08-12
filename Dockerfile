# Multi-stage build for optimization
FROM openjdk:17-jdk-slim AS builder

WORKDIR /app

# Gradle wrapper와 필수 파일 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

# 의존성 사전 다운로드 (캐시 최적화)
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon

# 소스 코드 복사 및 빌드
COPY src src
RUN ./gradlew build -x test --no-daemon

# Production 이미지
FROM openjdk:17-jdk-slim

WORKDIR /app

# 필수 패키지 설치 (모니터링용)
RUN apt-get update && apt-get install -y \
    curl \
    htop \
    && rm -rf /var/lib/apt/lists/*

# JAR 파일 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 로그 디렉토리 생성
RUN mkdir -p /app/logs

# 비루트 사용자 생성
RUN addgroup --system spring && adduser --system --group spring
RUN chown -R spring:spring /app
USER spring

# JVM 최적화 환경변수
ENV JAVA_OPTS="-Xms1g -Xmx2g -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+UseCompressedOops"

# 포트 노출
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# 애플리케이션 실행
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]