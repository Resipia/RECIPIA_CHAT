package com.recipia.chat.service

import com.recipia.chat.domain.ChatMessage
import com.recipia.chat.repository.ChatMessageRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class ChatMessageService(
        private val chatMessageRepository: ChatMessageRepository
) {

    fun saveMessage(chatMessage: ChatMessage): Mono<ChatMessage> {
        return chatMessageRepository.save(chatMessage)
    }

    fun getMessagesByRoomId(roomId: String): Flux<ChatMessage> {
        return chatMessageRepository.findAllByRoomId(roomId)
    }

}