package com.recipia.chat.config

import com.recipia.chat.socket.ChatWebSocketHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.HandlerMapping
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter

@Configuration
class WebSocketConfig(private val chatWebSocketHandler: ChatWebSocketHandler) {

    /**
     * WebSocket 요청을 처리할 엔드포인트와 그 엔드포인트를 처리할 WebSocketHandler를 매핑한다.
     * 여기서는 "/chat" 경로로 들어오는 WebSocket 연결 요청을 ChatWebSocketHandler가 처리하도록 설정한다.
     */
    @Bean
    fun handlerMapping(): HandlerMapping {
        val map = hashMapOf<String, WebSocketHandler>("/chat" to chatWebSocketHandler)

        return SimpleUrlHandlerMapping().apply {
            urlMap = map
            order = -1 // 우선순위 설정
        }
    }

    /**
     * WebSocket 요청을 처리하기 위한 어댑터로, Spring WebFlux에서 WebSocket 핸들러를 사용할 수 있게 해준다.
     * handlerAdapter() 메소드는 이 어댑터를 스프링 빈으로 등록하여 스프링이 WebSocket 핸들링을 할 수 있게 해준다.
     */
    @Bean
    fun handlerAdapter(): WebSocketHandlerAdapter {
        return WebSocketHandlerAdapter()
    }

}