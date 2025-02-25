package com.nexusorder.orderflow.controller.admin

import com.nexusorder.orderflow.model.storage.Product
import com.nexusorder.orderflow.service.storage.ProductStorageService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/admin/api/v1/products")
class AdminProductController(
    private val productStorageService: ProductStorageService
) {

    @GetMapping("/{id}")
    fun getProductById(@PathVariable id: String): Mono<Product> {
        return productStorageService.findById(id)
    }

    @GetMapping
    fun getAllProducts(): Flux<Product> {
        return productStorageService.findAll()
    }

    @PostMapping
    fun createProduct(@RequestBody product: Product): Mono<Product> {
        return productStorageService.save(product)
    }

    @GetMapping("/exists/{id}")
    fun productExists(@PathVariable id: String): Mono<Boolean> {
        return productStorageService.existsById(id)
    }

    @GetMapping("/shop/{shopId}")
    fun getProductsByShopId(@PathVariable shopId: String): Flux<Product> {
        return productStorageService.findAllByShopId(shopId)
    }
}
