package com.nexusorder.orderflow.controller

import com.nexusorder.orderflow.model.payload.ApiResponse
import com.nexusorder.orderflow.model.payload.ProductRequest
import com.nexusorder.orderflow.model.payload.ProductResponse
import com.nexusorder.orderflow.service.domain.ProductService
import com.nexusorder.orderflow.service.storage.ProductStorageService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/products")
class ProductController(
    private val productStorageService: ProductStorageService,
    private val productService: ProductService
) {

    @GetMapping("/{id}")
    fun findById(@PathVariable id: String): Mono<ApiResponse<ProductResponse>> {
        return productService.findById(id)
            .map { ApiResponse.success(it) }
    }

    @GetMapping
    fun findAll(): Mono<ApiResponse<List<ProductResponse>>> {
        return productService.findAll()
            .map { ApiResponse.success(it) }
    }

    @PostMapping
    fun save(@RequestBody request: ProductRequest): Mono<ApiResponse<ProductResponse>> {
        return productService.save(request)
            .map { ApiResponse.success(it) }
    }

    @GetMapping("/shop/{shopId}")
    fun findAllByShopId(@PathVariable shopId: String): Mono<ApiResponse<List<ProductResponse>>> {
        return productService.findAllByShopId(shopId)
            .map { ApiResponse.success(it) }
    }
}
