package com.recipia.chat.controller

import com.recipia.chat.domain.ChatMessage
import com.recipia.chat.service.ChatMessageService
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class ChatMessageController(
        private val chatMessageService: ChatMessageService
) {

    /**
     * 클라이언트가 /app/chat.send를 통해 메시지를 보내면, ChatMessageController의 sendMessage 메소드가 해당 메시지를 처리한다.
     * 이 메소드는 메시지를 MongoDB에 저장하고, 저장된 메시지를 /topic/public으로 전송하여 구독자에게 메시지를 브로드캐스트한다.
     */
    @MessageMapping("/chat.send")
    @SendTo("/topic/public")
    fun sendMessage(chatMessage: ChatMessage): Mono<ChatMessage> {
        return chatMessageService.saveMessage(chatMessage)
            .doOnNext { // 메시지 저장 후 처리
            }
    }

}