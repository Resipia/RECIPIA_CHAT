package com.recipia.chat.repository

import com.recipia.chat.domain.ChatRoom
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface ChatRoomRepository : ReactiveMongoRepository<ChatRoom, String> {
    fun findByRoomIdentifier(roomIdentifier: String): Mono<ChatRoom>

    fun findAllByMemberIdsContaining(memberId: String): Flux<ChatRoom>
}
