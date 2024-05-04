package com.recipia.chat.config.socket

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer


/**
 * WebSocketConfig 클래스에서 STOMP 웹소켓 연결을 위한 엔드포인트(/ws)와 메시지 브로커(/topic)를 설정한다.
 * 이 설정은 클라이언트가 서버에 연결하고 메시지를 주고받을 수 있는 구조를 제공한다.
 */
@EnableWebSocketMessageBroker
@Configuration
class WebSocketConfig : WebSocketMessageBrokerConfigurer {

    /**
     * 클라이언트가 웹소켓 연결을 시작할 수 있는 엔드포인트를 등록합니다.
     * withSockJS()는 SockJS 클라이언트와의 호환성을 위해 추가됩니다.
     * SockJS는 웹소켓을 지원하지 않는 브라우저에서도 백업 옵션으로 폴링 등의 기술을 사용할 수 있게 해줍니다.
     */
    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/recipia-websocket") // 클라이언트가 웹소켓 연결을 시작할 수 있는 엔드포인트를 등록합니다.
            .setAllowedOrigins("*") // 모든 도메인에서의 접속을 허용합니다.
            .withSockJS() // SockJS 지원을 활성화합니다.
    }

    /**
     * configureMessageBroker 메소드에서는 메시지를 발행(publish)하고 구독(subscribe)할 때 사용할 prefix를 설정한다.
     * /app으로 시작하는 목적지(destination)을 가진 메시지는 애플리케이션 내부에서 처리될 것이며,
     * /topic 또는 /queue로 시작하는 목적지를 가진 메시지는 브로커를 통해 구독자들에게 전달된다.
     * /topic은 주로 1:N 통신에 사용되며, /queue는 1:1 통신에 적합하다.
     */
    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        // 메시지를 구독하는 요청의 prefix를 설정합니다.
        registry.setApplicationDestinationPrefixes("/app")

        // 단순 메모리 기반 메시지 브로커가 해당 주제를 클라이언트에게 전달하도록 설정합니다.
        // 클라이언트가 메시지를 받을 endpoint의 prefix를 설정합니다.
        registry.enableSimpleBroker("/topic", "/queue") // 두 개의 메시지 브로커를 활성화한다.
    }

}