package com.recipia.chat.controller

import com.recipia.chat.domain.ChatRoom
import com.recipia.chat.service.ChatRoomService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@RestController
class ChatRoomController(
        private val chatRoomService:ChatRoomService
) {

    /**
     * 채팅방 목록 조회
     */
    @GetMapping("/userChatRooms")
    fun getUserChatRooms(@RequestParam memberId: String): Flux<ChatRoom> {
        return chatRoomService.getUserChatRooms(memberId)
    }

    /**
     * 클라이언트가 채팅방 정보를 요청하면, getChatRoom 메소드가 호출된다.
     * 이 메소드는 주어진 멤버 ID를 기반으로 채팅방을 조회하거나 새로운 채팅방을 생성한다.
     */
    @GetMapping("/chatRoom")
    fun getChatRoom(
        @RequestParam creatorId: String,
        @RequestParam participantId: String
    ): Mono<ChatRoom> {
        return chatRoomService.getOrCreateChatRoom(creatorId, participantId)
                .doOnNext { chatRoom ->
                    // Mono가 데이터를 발행했을 때 실행되는 코드
                    // 여기서 필요한 로직을 수행하면 돼. 예를 들어, 로깅을 할 수도 있고, 다른 처리를 할 수도 있어.
                }
                .subscribeOn(Schedulers.boundedElastic()) // 이 부분은 비동기 처리를 위해 설정한 스케줄러
    }

}