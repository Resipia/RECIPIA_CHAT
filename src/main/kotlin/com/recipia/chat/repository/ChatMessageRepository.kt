package com.recipia.chat.repository

import com.recipia.chat.domain.ChatMessage
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux

interface ChatMessageRepository: ReactiveMongoRepository<ChatMessage, String> {
    fun findAllByRoomId(roomId: String): Flux<ChatMessage>
}