package com.nexusorder.orderflow.service.domain

import com.nexusorder.orderflow.model.payload.CategoryResponse
import com.nexusorder.orderflow.service.storage.CategoryStorageService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CategoryService(
    private val categoryStorageService: CategoryStorageService
) {

    fun findAll(): Mono<List<CategoryResponse>> {
        return categoryStorageService.findAll()
            .map { CategoryResponse.from(it) }
            .collectList()
    }
}
