package com.recipia.chat.config.security

import com.recipia.chat.config.security.filter.JwtAuthorizationFilter
import com.recipia.chat.config.security.jwt.TokenValidator
import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import java.nio.charset.StandardCharsets
import javax.crypto.spec.SecretKeySpec

/**
 * 시큐리티 설정
 */
@Configuration
class SecurityConfig(
        private val tokenValidator: TokenValidator
) {

    companion object {
        // JWT 토큰 생성을 위한 비밀키 선언
        private const val jwtSecretKey = "thisIsASecretKeyUsedForJwtTokenGenerationAndItIsLongEnoughToMeetTheRequirementOf256Bits"
        val hmacSecretKey = SecretKeySpec(jwtSecretKey.toByteArray(StandardCharsets.UTF_8), "HMACSHA256")
    }

    /**
     * 이 메서드는 정적 자원에 대해 보안을 적용하지 않도록 설정한다.
     * 정적 자원은 보통 HTML, CSS, JavaScript, 이미지 파일 등을 의미하며, 이들에 대해 보안을 적용하지 않음으로써 성능을 향상시킬 수 있다.
     */
    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer = WebSecurityCustomizer { web ->
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations())
    }

    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain = http
            .csrf { csrf -> csrf.disable() }
            .authorizeHttpRequests { authorize ->
                authorize
                        .requestMatchers(
                                AntPathRequestMatcher("/resources/**"),
                                AntPathRequestMatcher("/health"),
                                AntPathRequestMatcher("/actuator/*"),
                                AntPathRequestMatcher("/actuator")
                        ).permitAll()
                        .anyRequest().authenticated()
            }
            .oauth2ResourceServer { oauth2 ->
                oauth2.jwt { jwt ->
                    jwt.decoder(jwtDecoder())
                }
            }
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .addFilterBefore(jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter::class.java)
            .build()

    /**
     * "JWT 토큰을 통해 사용자를 인증한다." -> 이 메서드는 JWT 인증 필터를 생성한다.
     * JWT 인증 필터는 요청 헤더의 JWT 토큰을 검증하고, 토큰이 유효하면 토큰에서 사용자의 정보와 권한을 추출하여 SecurityContext에 저장한다.
     */
    @Bean
    fun jwtAuthorizationFilter(): JwtAuthorizationFilter {
        return JwtAuthorizationFilter(tokenValidator)
    }

    /**
     * JWT 디코더를 생성하는 메서드.
     */
    @Bean
    fun jwtDecoder(): JwtDecoder {
        return NimbusJwtDecoder.withSecretKey(hmacSecretKey).build()
    }

}