package com.recipia.chat.exception

/**
 * 채팅 커스텀 예외 클래스
 */
class ChatApplicationException(
        val errorCode: ErrorCode
) : RuntimeException(errorCode.message) {  // 부모 클래스인 RuntimeException의 생성자를 호출


}