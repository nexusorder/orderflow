package com.nexusorder.orderflow.service.storage

import com.nexusorder.orderflow.model.storage.Category
import com.nexusorder.orderflow.repository.dynamodb.CategoryDynamoDBRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class CategoryStorageService(
    private val categoryRepository: CategoryDynamoDBRepository
) {

    fun findById(id: String): Mono<Category> {
        return categoryRepository.findById(id)
    }

    fun existsById(id: String): Mono<Boolean> {
        return categoryRepository.existsById(id)
    }

    fun findAll(): Flux<Category> {
        return categoryRepository.findAll()
    }

    fun save(entity: Category): Mono<Category> {
        return categoryRepository.save(entity)
    }

    fun findByKey(key: String): Mono<Category> {
        return categoryRepository.findByKey(key)
    }
}
