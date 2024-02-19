package com.recipia.chat.repository

import com.recipia.chat.domain.Message
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface MessageRepository: ReactiveMongoRepository<Message, String> {
}