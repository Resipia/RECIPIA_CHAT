package com.recipia.chat.socket

import com.recipia.chat.domain.Message
import com.recipia.chat.service.ChatRoomService
import com.recipia.chat.service.MessageService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Component
class ChatWebSocketHandler(
        private val chatRoomService: ChatRoomService,
        private val messageService: MessageService
) : WebSocketHandler {

    private val sessions: MutableMap<String, MutableList<WebSocketSession>> = mutableMapOf()

    /**
     * WebSocket 연결이 열릴 때마다 호출되는 메소드로, 클라이언트와의 세션을 나타낸다.
     * 클라이언트가 서버에 메시지를 보내면, 서버는 그 메시지를 받아서 "Echo: "라는 접두사를 붙인 후 같은 클라이언트에게 다시 보낸다.
     * 이는 일종의 에코 서비스로, 보낸 메시지가 그대로 돌아오는 것을 확인할 수 있게 한다.
     */
    override fun handle(session: WebSocketSession): Mono<Void> {
        val chatRoomId = session.handshakeInfo.uri.query // 채팅방 id 생성

        return session.receive() // 클라이언트로부터 메시지를 비동기적으로 받는다.
                .flatMap { webSocketMessage ->
                    val messageText = webSocketMessage.payloadAsText
                    val message = Message(
                            chatRoomId = chatRoomId,
                            senderId = "senderId",
                            message = messageText,
                            createdAt = LocalDateTime.now()
                    )

                    // 생성된 메시지 객체를 데이터베이스에 저장
                    messageService.saveMessage(message)
                            .flatMap { broadcast(chatRoomId, message) } // 해당 채팅방에 있는 모든 클라이언트에게 메시지를 브로드캐스트
                }.then()
    }

    /**
     * 주어진 chatRoomId에 해당하는 모든 세션에게 메시지를 전송하는 메서드다.
     * sessions 맵에서 해당 채팅방의 세션 리스트를 조회하고, 각 세션에 메시지를 전송한다.
     */
    private fun broadcast(chatRoomId: String, message: Message): Mono<Void> {
        sessions[chatRoomId]?.forEach { session ->
            session.send(Mono.just(session.textMessage(message.toString()))).subscribe()
        }
        return Mono.empty()
    }


}