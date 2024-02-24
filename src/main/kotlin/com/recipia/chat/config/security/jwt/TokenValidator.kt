package com.recipia.chat.config.security.jwt

import com.recipia.chat.logger
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException
import lombok.extern.slf4j.Slf4j
import org.springframework.security.oauth2.jwt.JwtException
import org.springframework.stereotype.Component

@Slf4j
@Component
class TokenValidator {

    val log = logger()

    /**
     * 토큰의 유효성을 검사하는 메서드
     */
    fun isValidToken(token: String?, tokenType: String): Boolean {
        try {
            val claims: Claims = TokenUtils.getClaimsFromToken(token)
            val type: String = claims.get("type", String::class.java)
            return type == tokenType
        } catch (jwtException: JwtException) {
            log.debug("JWT validation error: ${jwtException.message}")
            when (jwtException) {
                is ExpiredJwtException -> log.debug("Expired JWT token: {}", token)
                is MalformedJwtException -> log.debug("Malformed JWT token: {}", token)
                else -> log.debug("Other JWT error: {}", jwtException.javaClass.simpleName)
            }
            return false
        }
    }

}