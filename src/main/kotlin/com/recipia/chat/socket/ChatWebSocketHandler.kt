package com.recipia.chat.socket

import com.recipia.chat.domain.Message
import com.recipia.chat.service.ChatRoomService
import com.recipia.chat.service.MessageService
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono
import java.net.URI
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
        // session의 handshakeInfo로부터 URI를 가져온 후, 쿼리 스트링을 파싱합니다.
        val queryParams = URI(session.handshakeInfo.uri.toString()).query.split("&")
                .associate {
                    val (key, value) = it.split("=")
                    key to value
                }

        // "roomId" 쿼리 파라미터에서 chatRoomId를 추출합니다.
        val chatRoomId = queryParams["roomId"]

        // 채팅방 식별자가 없는 경우 에러 처리
        if (chatRoomId == null) {
            // 에러 처리 로직 (예: 세션 종료)
            return Mono.error(IllegalStateException("No chatRoomId provided"))
        }

        // WebSocket 연결이 인증된 사용자에 의해 생성된 것이라고 가정
        val authentication = SecurityContextHolder.getContext().authentication
        val senderId = if (authentication != null && authentication.isAuthenticated) {
            (authentication.principal as UserDetails).username
        } else {
            return Mono.error(IllegalStateException("User not authenticated"))
        }


        return session.receive() // 클라이언트로부터 메시지를 비동기적으로 받는다.
                .flatMap { webSocketMessage ->
                    val messageText = webSocketMessage.payloadAsText
                    val message = Message(
                            chatRoomId = chatRoomId,
                            senderId = senderId,
                            message = messageText,
                            createdAt = LocalDateTime.now()
                    )

                    // 메시지를 저장하고 해당 채팅방의 모든 클라이언트에게 메시지를 브로드캐스트합니다.
                    messageService.saveMessage(message)
                            .flatMap { broadcast(chatRoomId, message) }
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