package com.nexusorder.orderflow.controller

import com.nexusorder.orderflow.model.payload.ApiResponse
import com.nexusorder.orderflow.model.payload.LoginRequest
import com.nexusorder.orderflow.model.payload.ProfileResponse
import com.nexusorder.orderflow.model.payload.SignupRequest
import com.nexusorder.orderflow.service.domain.AuthService
import com.nexusorder.orderflow.service.domain.AuthService.Companion.LOGIN_KEY
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/login")
    fun login(@RequestBody @Valid request: LoginRequest, exchange: ServerWebExchange): Mono<ApiResponse<Boolean>> {
        return authService.login(request)
            .flatMap {
                exchange.session.map { session ->
                    session.attributes[LOGIN_KEY] = it.id
                    true
                }
            }.map {
                ApiResponse.success(it)
            }.switchIfEmpty(
                exchange.session.map { session ->
                    session.invalidate()
                }.thenReturn(
                    ApiResponse.success(false)
                )
            )
    }

    @PostMapping("/signup")
    fun signup(@RequestBody @Valid request: SignupRequest, exchange: ServerWebExchange): Mono<ApiResponse<Boolean>> {
        if (!request.isValid()) return Mono.just(ApiResponse.success(false))

        return authService.signup(request)
            .flatMap {
                exchange.session.map { session ->
                    session.attributes[LOGIN_KEY] = it.id
                    true
                }
            }.map {
                ApiResponse.success(it)
            }.switchIfEmpty(
                exchange.session.map { session ->
                    session.invalidate()
                }.thenReturn(
                    ApiResponse.success(false)
                )
            )
    }

    @PostMapping("/logout")
    fun logout(exchange: ServerWebExchange): Mono<ApiResponse<Boolean>> {
        return logoutInternal(exchange)
    }

    private fun logoutInternal(exchange: ServerWebExchange): Mono<ApiResponse<Boolean>> {
        return exchange.session.map {
            it.invalidate()
            true
        }.map {
            ApiResponse.success(it)
        }
    }

    @GetMapping("/profile")
    fun profile(exchange: ServerWebExchange): Mono<ApiResponse<ProfileResponse>> {
        return exchange.session
            .mapNotNull {
                it.attributes[LOGIN_KEY] as String?
            }.flatMap { id ->
                authService.profile(id!!)
                    .map { ApiResponse.success(it) }
            }.switchIfEmpty(
                exchange.session.map { session ->
                    session.invalidate()
                }.thenReturn(
                    ApiResponse.success(null)
                )
            )
    }
}
