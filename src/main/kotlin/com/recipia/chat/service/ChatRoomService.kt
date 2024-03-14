package com.recipia.chat.service

import com.recipia.chat.domain.ChatRoom
import com.recipia.chat.repository.ChatRoomRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime

/**
 * 채팅방 서비스
 */
@Service
class ChatRoomService(
        private val chatRoomRepository: ChatRoomRepository
) {

    /**
     * 채팅방이 이미 존재하는 경우 기존 채팅방을 사용하고, 존재하지 않는 경우 새로운 채팅방을 생성한다.
     * 채팅 메시지는 MongoDB에 저장하여, 채팅방에 참여하는 모든 사용자가 이전 메시지를 볼 수 있도록 한다.
     */
    fun getOrCreateChatRoom(creatorId: String, participantId: String): Mono<ChatRoom> {
        val memberIds = setOf(creatorId, participantId)
        val roomIdentifier = memberIds.sorted().joinToString(separator = ":")

        return chatRoomRepository.findByRoomIdentifier(roomIdentifier)
            .switchIfEmpty(Mono.defer {
                val newChatRoom = ChatRoom(
                    roomIdentifier = roomIdentifier,
                    memberIds = memberIds.toMutableSet(),
                    creatorId = creatorId, // 채팅방을 생성한 사용자 ID
                    participantId = participantId, // 채팅방에 참여한 다른 사용자 ID
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                )
                chatRoomRepository.save(newChatRoom)
            })
    }

    /**
     * 채팅방 목록을 조회
     */
    fun getUserChatRooms(memberId: String): Flux<ChatRoom> {
        return chatRoomRepository.findAllByMemberIdsContaining(memberId)
    }
}