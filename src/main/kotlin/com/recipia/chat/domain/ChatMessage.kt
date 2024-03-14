package com.recipia.chat.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "chatMessages")
data class ChatMessage(

    @Id
    val id: String? = null,
    val roomId: String,
    val senderId: String,
    val message: String,
    val createdAt: LocalDateTime = LocalDateTime.now()

)