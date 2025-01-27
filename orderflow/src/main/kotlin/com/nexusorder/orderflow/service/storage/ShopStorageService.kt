package com.nexusorder.orderflow.service.storage

import com.nexusorder.orderflow.model.storage.Shop
import com.nexusorder.orderflow.repository.dynamodb.ShopDynamoDBRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class ShopStorageService(
    private val shopRepository: ShopDynamoDBRepository
) {

    fun findById(id: String): Mono<Shop> {
        return shopRepository.findById(id)
    }

    fun existsById(id: String): Mono<Boolean> {
        return shopRepository.existsById(id)
    }

    fun findAll(): Flux<Shop> {
        return shopRepository.findAll()
    }

    fun save(entity: Shop): Mono<Shop> {
        return shopRepository.save(entity)
    }
}
