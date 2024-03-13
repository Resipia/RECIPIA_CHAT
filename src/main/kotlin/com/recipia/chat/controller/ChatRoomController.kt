package com.recipia.chat.controller

import com.recipia.chat.domain.ChatRoom
import com.recipia.chat.service.ChatRoomService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@RestController
class ChatRoomController(
        private val chatRoomService:ChatRoomService
) {

    @GetMapping("/chatRoom")
    fun getChatRoom(memberIds: Set<String>): Mono<ChatRoom> {
        return chatRoomService.getOrCreateChatRoom(memberIds)
                .doOnNext { chatRoom ->
                    // Mono가 데이터를 발행했을 때 실행되는 코드
                    // 여기서 필요한 로직을 수행하면 돼. 예를 들어, 로깅을 할 수도 있고, 다른 처리를 할 수도 있어.
                }
                .subscribeOn(Schedulers.boundedElastic()) // 이 부분은 비동기 처리를 위해 설정한 스케줄러
    }

}