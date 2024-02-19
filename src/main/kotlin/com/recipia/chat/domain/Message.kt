package com.recipia.chat.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "messages")
data class Message(

    @Id
    val id: String? = null, // 메시지 고유 ID. MongoDB가 자동으로 생성해주므로 nullable로 선언.
    val chatRoomId: String, // 채팅방 ID
    val senderId: String,   // 멤버 서버의 사용자 ID
    val message: String,    // 메시지 내용
    val createdAt: LocalDateTime

)