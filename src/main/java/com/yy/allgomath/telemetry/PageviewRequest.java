package com.yy.allgomath.telemetry;

/** 페이지뷰 비컨 페이로드. route는 라우트 패턴, cid는 브라우저 로컬 UUID. */
public record PageviewRequest(String route, String cid) {
}
