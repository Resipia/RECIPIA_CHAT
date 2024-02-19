package com.recipia.chat.socket

import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono

@Component
class ChatWebSocketHandler: WebSocketHandler {

    /**
     * WebSocket 연결이 열릴 때마다 호출되는 메소드로, 클라이언트와의 세션을 나타낸다.
     * 클라이언트가 서버에 메시지를 보내면, 서버는 그 메시지를 받아서 "Echo: "라는 접두사를 붙인 후 같은 클라이언트에게 다시 보낸다.
     * 이는 일종의 에코 서비스로, 보낸 메시지가 그대로 돌아오는 것을 확인할 수 있게 한다.
     */
    override fun handle(session: WebSocketSession): Mono<Void> {
        return session.receive()
                .map { msg -> "Echo: ${msg.payloadAsText}" } // 메시지 변환
                .map(session::textMessage) // 변환된 메시지를 WebSocketMessage로 변환
                .let(session::send) // 변환된 메시지들을 send 메서드로 전송
    }

}