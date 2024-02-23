package com.recipia.chat.service

import com.recipia.chat.domain.Message
import com.recipia.chat.repository.MessageRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class MessageService(
        private val messageRepository: MessageRepository
) {

    fun saveMessage(message: Message): Mono<Message> {
        return messageRepository.save(message)
    }

    // 특정 채팅방 ID에 대한 모든 메시지를 조회한다.
    fun getMessagesByChatRoomId(chatRoomId: String): Flux<Message> {
        return messageRepository.findByChatRoomId(chatRoomId)
    }

}