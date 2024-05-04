package com.recipia.chat.config.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter
import org.springframework.security.web.server.SecurityWebFilterChain
import java.nio.charset.StandardCharsets
import javax.crypto.spec.SecretKeySpec

@EnableWebFluxSecurity
@Configuration
class SecurityConfig() {

    companion object {
        // JWT 토큰 생성을 위한 비밀키 선언
        private const val jwtSecretKey = "thisIsASecretKeyUsedForJwtTokenGenerationAndItIsLongEnoughToMeetTheRequirementOf256Bits"
        val hmacSecretKey = SecretKeySpec(jwtSecretKey.toByteArray(StandardCharsets.UTF_8), "HMACSHA256")
    }

    /**
     * oauth2ResourceServer 설정을 통해, 들어오는 요청에 대한 JWT 토큰 검증과 인증 과정이 자동으로 이루어지며,
     * 유효한 토큰이 확인되면, 해당 토큰에서 추출한 인증 정보가 ReactiveSecurityContext에 설정된다.
     */
    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .csrf((ServerHttpSecurity.CsrfSpec::disable))
            .formLogin((ServerHttpSecurity.FormLoginSpec::disable))
            .httpBasic((ServerHttpSecurity.HttpBasicSpec::disable))
            .authorizeExchange { exchanges ->
                exchanges
                    .pathMatchers("/resources/**", "/health", "/actuator/**").permitAll()
                    .anyExchange().authenticated()
            }
            .oauth2ResourceServer { oauth2 -> // JWT 인증 메커니즘 활성화
                oauth2.jwt { jwt ->
                    jwt.jwtDecoder(jwtDecoder())
                    jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())
                }
            }
            .build()
    }

    /**
     * ReactiveJwtDecoder는 JWT 토큰을 비동기적으로 디코딩하고 검증하는 역할을 한다.
     * 이 과정은 리액티브 스트림을 사용해서 이루어지기 때문에, WebFlux의 논블로킹 모델에 잘 어울린다.
     * 디코딩된 JWT는 Mono<Jwt> 타입으로 반환되며, 이후에 인증 과정을 위해 사용된다.
     */
    @Bean
    fun jwtDecoder(): ReactiveJwtDecoder {
        return NimbusReactiveJwtDecoder.withSecretKey(hmacSecretKey).build()
    }

    /**
     * ReactiveJwtAuthenticationConverter는 ReactiveJwtDecoder를 통해 디코딩된 JWT로부터 인증 정보를 추출하고,
     * 이를 Spring Security가 이해할 수 있는 Authentication 객체로 변환하는 역할을 한다.
     * 이 역시 리액티브 방식으로 처리되며, 변환된 Authentication 객체는 Mono<Authentication> 타입으로 반환된다.
     * 이 과정을 통해 생성된 Authentication 객체는 ReactiveSecurityContext에 자동으로 설정되어,
     * 애플리케이션의 다른 부분에서 인증된 사용자의 정보를 비동기적으로 조회할 수 있게 한다.
     */
    @Bean
    fun jwtAuthenticationConverter(): ReactiveJwtAuthenticationConverter {
        return ReactiveJwtAuthenticationConverter()
    }

}