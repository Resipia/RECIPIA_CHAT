package com.recipia.chat.config.socket

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.web.socket.messaging.*

/**
 * WebSocketEventHandler는 STOMP 웹소켓 연결 및 구독 이벤트를 로깅하는 데 사용할 수 있는 선택적 구성 요소다.
 * 이 컴포넌트는 연결 및 구독의 생명주기 동안 발생하는 다양한 이벤트를 처리하여,
 * 예를 들어, 사용자의 연결 및 구독 상태를 모니터링하거나 디버깅 정보를 제공하는 데 도움이 될 수 있다.
 */
@Component
class WebSocketEventHandler {

    private val logger = KotlinLogging.logger {}

    @EventListener
    fun handleSessionNewConnection(event: SessionConnectEvent) {
        logger.info { "새로운 웹소켓 연결 시도: ${event.message}" }
    }

    @EventListener
    fun handleSessionConnected(event: SessionConnectedEvent) {
        logger.info { "웹소켓 연결 성공: ${event.message}" }
    }

    @EventListener
    fun handleSessionDisconnect(event: SessionDisconnectEvent) {
        logger.info { "웹소켓 연결 해제: ${event.message}" }
    }

    @EventListener
    fun handleSessionSubscribe(event: SessionSubscribeEvent) {
        logger.info { "웹소켓 구독 시작: ${event.message}" }
    }

    @EventListener
    fun handleSessionUnsubscribe(event: SessionUnsubscribeEvent) {
        logger.info { "웹소켓 구독 종료: ${event.message}" }
    }

}