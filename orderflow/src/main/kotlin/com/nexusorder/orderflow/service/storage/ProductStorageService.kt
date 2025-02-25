package com.nexusorder.orderflow.service.storage

import com.nexusorder.orderflow.model.storage.Product
import com.nexusorder.orderflow.repository.dynamodb.ProductDynamoDBRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class ProductStorageService(
    private val productRepository: ProductDynamoDBRepository
) {

    fun findById(id: String): Mono<Product> {
        return productRepository.findById(id)
    }

    fun existsById(id: String): Mono<Boolean> {
        return productRepository.existsById(id)
    }

    fun findAll(): Flux<Product> {
        return productRepository.findAll()
    }

    fun save(entity: Product): Mono<Product> {
        return productRepository.save(entity)
    }

    fun findAllByShopId(shopId: String): Flux<Product> {
        return productRepository.findAllByShopId(shopId)
    }
}
