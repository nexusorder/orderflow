package com.nexusorder.orderflow.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
@EnableWebFluxSecurity
class SecurityConfig {

    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .authorizeExchange { exchanges ->
                exchanges
                    .pathMatchers("/api/**").permitAll()
                    .pathMatchers("/health").permitAll()
                    .anyExchange().authenticated()
            }
            .httpBasic(withDefaults())
            .formLogin(withDefaults())
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .build()
    }
}
