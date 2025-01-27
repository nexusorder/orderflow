package com.nexusorder.orderflow.service.domain

import com.nexusorder.orderflow.model.payload.ProductRequest
import com.nexusorder.orderflow.model.payload.ProductResponse
import com.nexusorder.orderflow.model.storage.Product
import com.nexusorder.orderflow.service.storage.ProductStorageService
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.PathVariable
import reactor.core.publisher.Mono

@Service
class ProductService(
    private val productStorageService: ProductStorageService
) {

    fun findById(@PathVariable id: String): Mono<ProductResponse> {
        return productStorageService.findById(id)
            .map { ProductResponse.from(it) }
    }

    fun findAll(): Mono<List<ProductResponse>> {
        return productStorageService.findAll()
            .map { ProductResponse.from(it) }
            .collectList()
    }

    fun save(request: ProductRequest): Mono<ProductResponse> {
        return productStorageService.save(Product.from(request))
            .map { ProductResponse.from(it) }
    }

    fun findAllByShopId(shopId: String): Mono<List<ProductResponse>> {
        return productStorageService.findAllByShopId(shopId)
            .map { ProductResponse.from(it) }
            .collectList()
    }
}
