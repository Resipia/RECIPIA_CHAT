package com.recipia.chat.config.security.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.security.Key
import java.time.Instant
import java.util.*

/**
 * jwt 전용 유틸 클래스
 */
@Component
class TokenUtils {

    companion object {
        // JWT 비밀 키, HMAC-SHA 알고리즘을 위한 키 생성
        private val jwtSecretKey = "thisIsASecretKeyUsedForJwtTokenGenerationAndItIsLongEnoughToMeetTheRequirementOf256Bits"
        private val key: Key = Keys.hmacShaKeyFor(jwtSecretKey.toByteArray(StandardCharsets.UTF_8))

        // 액세스 토큰 유형 상수
        const val ACCESS_TOKEN_TYPE: String = "access"

        // 토큰에서 역할(role)을 가져오는 메서드
        fun getRoleFromToken(token: String?): String {
            val claims: Claims = getClaimsFromToken(token)
            return claims.get("role", String::class.java)
        }

        // 토큰에서 Claims 정보를 추출하는 메서드
        fun getClaimsFromToken(token: String?): Claims {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build().parseClaimsJws(token).getBody()
        }

        // 토큰에서 Claims 정보를 맵 형태로 가져오는 메서드
        fun getClaimsMapFromToken(token: String?): Map<String, Any> {
            return getClaimsFromToken(token)
        }

        /**
         * "exp" 클레임을 Instant 타입으로 반환할 수 있는 메서드
         * 원인: JWT의 "exp" (유효기간) 클레임은 Instant 타입이어야 함. Integer로 설정하면 문제가 발생할 수 있음.
         * 해결: "exp" 클레임의 값을 Instant 타입으로 변환해야 함.
         */
        fun getExpirationFromToken(token: String?): Instant {
            val claims: Claims = getClaimsFromToken(token)
            return claims.get("exp", Date::class.java).toInstant()
        }

        // 토큰의 클레임에서 "memberId"를 꺼내는 메서드
        fun getMemberIdFromToken(token: String?): Long {
            val claims: Claims = getClaimsFromToken(token)
            return claims.get("memberId").toString().toLong()
        }

        // 토큰의 클레임에서 "nickname"을 꺼내는 메서드
        fun getNicknameFromToken(token: String?): String {
            val claims: Claims = getClaimsFromToken(token)
            return claims.get("nickname").toString()
        }

        // 토큰에서 사용자 이름을 가져오는 메서드
        fun getUsernameFromToken(token: String?): String {
            val claims: Claims = getClaimsFromToken(token)
            return claims.get("username", String::class.java)
        }
    }


}