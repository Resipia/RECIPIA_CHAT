package com.recipia.chat.controller

import com.recipia.chat.domain.Message
import com.recipia.chat.service.MessageService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
class MessageController(
        private val messageService: MessageService
) {

    /**
     * 채팅방 생성
     */
    @GetMapping("/chatrooms/{chatRoomId}/messages")
    fun getMessages(@PathVariable chatRoomId: String): Flux<Message> {
        return messageService.getMessagesByChatRoomId(chatRoomId)
    }

}