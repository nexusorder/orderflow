package com.nexusorder.orderflow.controller

import com.nexusorder.orderflow.model.payload.ApiResponse
import com.nexusorder.orderflow.model.payload.ShopRequest
import com.nexusorder.orderflow.model.payload.ShopResponse
import com.nexusorder.orderflow.service.domain.ShopService
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/shops")
class ShopController(
    private val shopService: ShopService
) {

    @GetMapping("/{id}")
    fun findById(@PathVariable id: String): Mono<ResponseEntity<ApiResponse<ShopResponse>>> {
        return shopService.findById(id)
            .map { ResponseEntity.ok().body(ApiResponse.success(it)) }
            .defaultIfEmpty(ResponseEntity.notFound().build())
    }

    @GetMapping
    fun findAll(): Mono<ApiResponse<List<ShopResponse>>> {
        return shopService.findAll()
            .map { ApiResponse.success(it) }
    }

    @PostMapping
    fun save(@RequestBody @Valid request: ShopRequest): Mono<ApiResponse<ShopResponse>> {
        return shopService.save(request)
            .map { ApiResponse.success(it) }
    }

    @GetMapping("/recommend")
    fun recommend(@RequestParam @Valid @Min(1) count: Int = 1): Mono<ApiResponse<List<ShopResponse>>> {
        return shopService.recommend(count)
            .map { ApiResponse.success(it) }
    }
}
