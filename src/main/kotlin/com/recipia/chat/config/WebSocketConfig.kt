package com.recipia.chat.config

import com.recipia.chat.socket.ChatWebSocketHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.HandlerMapping
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter

/**
 * WebSocket 통신을 사용하는 경우, 일반적인 HTTP 요청을 처리하는 컨트롤러(Controller)와는 다른 접근 방식이 필요하다.
 * WebSocket은 연결 지향적이며, 한 번의 연결 설정 후 지속적인 양방향 통신을 가능하게 한다.
 * 따라서, WebSocket 통신을 위해서는 WebSocketHandler를 사용하여 연결을 관리하고 메시지를 처리한다.
 */
@Configuration
class WebSocketConfig(
        private val chatWebSocketHandler: ChatWebSocketHandler
) {

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