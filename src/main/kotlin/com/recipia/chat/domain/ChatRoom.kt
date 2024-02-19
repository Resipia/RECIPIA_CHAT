package com.recipia.chat.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "chatRooms")
data class ChatRoom(

    @Id
    val id: String? = null,  // 채팅방 고유 ID. MongoDB가 자동으로 생성해주므로 nullable로 선언.
    val memberIds: MutableSet<String> = HashSet(), // 멤버 서버의 사용자 ID 목록. MutableSet을 사용하여 수정 가능하도록 함.
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null

)