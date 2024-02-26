package com.recipia.chat.exception

/**
 * 커스텀 에러코드 정의
 */
enum class ErrorCode(
        val status: Int,
        val code: Int,
        val message: String
) {
    // 10000. 로그인 관련 에러 (JWT 포함)
    USER_NOT_FOUND(404, 10001, "유저를 찾을 수 없습니다."),
    INVALID_JWT(401, 10002, "토큰이 유효하지 않습니다."),
    EXPIRED_JWT(401, 10003, "JWT 토큰이 만료되었습니다."),
    MISSING_JWT(401, 10004, "JWT 토큰이 누락되었습니다."),

    // 80000. 공통 에러
    IO_ERROR(404, 80001, "INPUT/OUTPUT ERROR"),
    BAD_REQUEST(400, 80002, "잘못된 요청"),
    FILTER_USERNAME_PASSWORD_AUTHENTICATION_TOKEN(404, 80003, "filter attemptAuthentication 인증 에러"),
    EVENT_NOT_FOUND(404, 80004, "이벤트 저장소에 해당 이벤트를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(500, 80005, "서버 내부 오류"),
    NULL_POINTER_EXCEPTION(500, 80006, "Null 참조 오류"),
    ILLEGAL_ARGUMENT_EXCEPTION(400, 80007, "부적절한 인자 오류"),
    ;
}