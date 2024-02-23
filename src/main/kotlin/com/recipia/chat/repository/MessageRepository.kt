package com.recipia.chat.repository

import com.recipia.chat.domain.Message
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux

interface MessageRepository: ReactiveMongoRepository<Message, String> {
    fun findByChatRoomId(chatRoomId: String): Flux<Message>
}