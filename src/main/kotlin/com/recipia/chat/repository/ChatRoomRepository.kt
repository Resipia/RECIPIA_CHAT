package com.recipia.chat.repository

import com.recipia.chat.domain.ChatRoom
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

interface ChatRoomRepository : ReactiveMongoRepository<ChatRoom, String> {
    fun findByRoomIdentifier(roomIdentifier: String): Mono<ChatRoom>
}
