package com.nexusorder.orderflow.controller

import com.nexusorder.orderflow.model.payload.ApiResponse
import com.nexusorder.orderflow.model.payload.OrderRequest
import com.nexusorder.orderflow.model.payload.OrderResponse
import com.nexusorder.orderflow.service.domain.AuthService.Companion.LOGIN_KEY
import com.nexusorder.orderflow.service.domain.OrderService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/orders")
class OrderController(
    private val orderService: OrderService
) {

    @GetMapping("/{id}")
    fun findByIdAndMemberId(
        @PathVariable id: String,
        exchange: ServerWebExchange
    ): Mono<ResponseEntity<ApiResponse<OrderResponse>>> {
        return exchange.session.mapNotNull { session ->
            session.attributes[LOGIN_KEY] as String?
        }.flatMap { memberId ->
            orderService.findByIdAndMemberId(id, memberId!!)
                .map {
                    ResponseEntity.ok(
                        ApiResponse.success(it)
                    )
                }.defaultIfEmpty(
                    ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(
                            ApiResponse.error(
                                null,
                                HttpStatus.NOT_FOUND.value(),
                                "Not Found"
                            )
                        )
                )
        }.unauthorizedIfEmpty()
    }

    @GetMapping
    fun findAllByMemberId(exchange: ServerWebExchange): Mono<ResponseEntity<ApiResponse<List<OrderResponse>>>> {
        return exchange.session.mapNotNull { session ->
            session.attributes[LOGIN_KEY] as String?
        }.flatMap { memberId ->
            orderService.findAllByMemberId(memberId!!)
                .map {
                    ResponseEntity.ok(
                        ApiResponse.success(it)
                    )
                }
        }.unauthorizedIfEmpty()
    }

    @PostMapping
    fun save(
        @RequestBody request: OrderRequest,
        exchange: ServerWebExchange
    ): Mono<ResponseEntity<ApiResponse<String>>> {
        return exchange.session.mapNotNull { session ->
            session.attributes[LOGIN_KEY] as String?
        }.flatMap { memberId ->
            orderService.save(memberId!!, request)
                .map { order ->
                    ResponseEntity.status(HttpStatus.OK)
                        .body(
                            ApiResponse.success(order.id)
                        )
                }
        }.unauthorizedIfEmpty()
    }

    private fun <T> Mono<ResponseEntity<ApiResponse<T>>>.unauthorizedIfEmpty(): Mono<ResponseEntity<ApiResponse<T>>> {
        return this.defaultIfEmpty(
            ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(
                    ApiResponse.error(
                        null,
                        HttpStatus.UNAUTHORIZED.value(),
                        "Unauthorized"
                    )
                )
        )
    }
}
