package com.recipia.chat.repository

import com.recipia.chat.domain.ChatRoom
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface ChatRoomRepository: ReactiveMongoRepository<ChatRoom, String> {
}