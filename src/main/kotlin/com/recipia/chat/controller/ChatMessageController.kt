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
     * @SendTo("/topic/public")는 처리된 메시지를 어디로 전송할지 결정한다.
     * 메시지가 /topic/public으로 전송되어, 이 주제를 구독하는 모든 클라이언트에게 브로드캐스트되는것이다.
     */
    @MessageMapping("/chat.send")
    @SendTo("/topic/public")
    fun sendMessage(chatMessage: ChatMessage): Mono<ChatMessage> {
        return chatMessageService.saveMessage(chatMessage)
            .doOnNext { // 메시지 저장 후 처리
            }
    }

}