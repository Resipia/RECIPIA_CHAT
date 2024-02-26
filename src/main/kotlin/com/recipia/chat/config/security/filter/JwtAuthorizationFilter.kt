package com.recipia.chat.config.security.filter

import com.recipia.chat.config.security.jwt.TokenUtils
import com.recipia.chat.config.security.jwt.TokenValidator
import com.recipia.chat.logger
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import java.time.Instant

/**
 * jwt 검사를 위한 필터
 */
class JwtAuthorizationFilter(
        private val tokenValidator: TokenValidator
): OncePerRequestFilter() {

    val log = logger()

    // 상수로 선언된 액세스 토큰 유형
    private val ACCESS_TOKEN_TYPE = "access"

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        // 클라이언트로부터 전달받은 Authorization 헤더값을 추출
        var token = request.getHeader("Authorization")
        log.debug("Authorization header: {}", token)

        // 토큰이 Bearer 스키마를 따르는지 확인
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7) // "Bearer " 접두어 제거
            log.debug("Extracted JWT token: {}", token)

            // 토큰 유효성 검사
            if (validateToken(token)) {
                // 토큰에서 클레임 추출
                val claimsMap: Map<String, Any?> = TokenUtils.getClaimsMapFromToken(token) // Replace this with your token extraction logic
                val expiration: Instant = TokenUtils.getExpirationFromToken(token)
                val memberIdFromToken: Long = TokenUtils.getMemberIdFromToken(token)
                val nicknameFromToken: String = TokenUtils.getNicknameFromToken(token)

                // 인증 객체에서 사용할 Jwt 객체 생성하기
                val jwt = Jwt.withTokenValue(token)
                        .header("typ", "JWT")
                        .claims { claims: MutableMap<String?, Any?> -> claims.putAll(claimsMap) }
                        .claim("exp", expiration)
                        .claim("memberId", memberIdFromToken)
                        .claim("nickname", nicknameFromToken)
                        .build()

                // 인증 객체 생성
                val authenticationToken = UsernamePasswordAuthenticationToken(jwt, null, extractAuthorities(token))

                // SecurityContext에 인증 객체 넣어주기 (이렇게 하면 꺼내다 쓸 수 있다.)
                SecurityContextHolder.getContext().authentication = authenticationToken
            } else {
                log.debug("Invalid JWT token: {}", token)
            }
        } else {
            log.debug("No JWT token found in request headers")
        }
        // 필터 체인 실행
        filterChain.doFilter(request, response)
    }

    // 토큰의 유효성을 검증하는 메서드
    private fun validateToken(token: String): Boolean {
        return tokenValidator.isValidToken(token, ACCESS_TOKEN_TYPE)
    }

    // 토큰에서 권한 정보를 추출
    private fun extractAuthorities(token: String): List<SimpleGrantedAuthority> {
        val role: String = TokenUtils.getRoleFromToken(token) // Replace this with your token extraction logic
        return listOf(SimpleGrantedAuthority(role))
    }

}