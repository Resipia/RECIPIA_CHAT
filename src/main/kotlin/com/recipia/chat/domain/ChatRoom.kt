package com.recipia.chat.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "chatRooms")
data class ChatRoom(

    @Id
    val id: String? = null,  // 채팅방 고유 ID. MongoDB가 자동으로 생성해주므로 nullable로 선언.
    val roomIdentifier: String, // 채팅방을 식별하는 데 사용되는 필드 (1:1 채팅방을 식별자)
    val memberIds: MutableSet<String> = HashSet(), // 멤버 서버의 사용자 ID 목록. MutableSet을 사용하여 수정 가능하도록 함.
    val creatorId: String, // 채팅방을 생성한 사용자 ID
    val participantId: String, // 채팅방에 참여한 다른 사용자 ID
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null

)