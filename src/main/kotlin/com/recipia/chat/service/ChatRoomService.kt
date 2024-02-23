package com.recipia.chat.service

import com.recipia.chat.domain.ChatRoom
import com.recipia.chat.repository.ChatRoomRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

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
    fun getOrCreateChatRoom(memberIds: Set<String>): Mono<ChatRoom> {
        // 두 사용자 ID를 알파벳 순으로 정렬하여 결합함으로써 roomIdentifier를 생성한다.
        // 이 방식은 두 사용자의 ID 조합이 항상 동일한 문자열을 생성하도록 보장한다.
        val roomIdentifier = memberIds.sorted().joinToString(separator = ":")

        // roomIdentifier를 사용하여 채팅방을 조회
        return chatRoomRepository.findByRoomIdentifier(roomIdentifier)
                .switchIfEmpty(Mono.defer {
                    // 해당 roomIdentifier를 가진 채팅방이 없는 경우 새로운 채팅방을 생성하고 저장
                    val newChatRoom = ChatRoom(roomIdentifier, memberIds.toMutableSet())
                    chatRoomRepository.save(newChatRoom)
                })
    }
}