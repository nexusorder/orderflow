package com.nexusorder.orderflow.service.storage

import com.nexusorder.orderflow.model.storage.Category
import com.nexusorder.orderflow.repository.dynamodb.CategoryDynamoDBRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import reactor.core.publisher.Flux
import reactor.kotlin.test.test

class CategoryStorageServiceTest {

    private lateinit var categoryStorageService: CategoryStorageService
    private lateinit var categoryRepository: CategoryDynamoDBRepository
    private val repository = mutableMapOf<String, Category>()

    @BeforeEach
    fun setUp() {
        categoryRepository = createMockCategoryRepository()
        categoryStorageService = CategoryStorageService(categoryRepository)
    }

    private fun createMockCategoryRepository(): CategoryDynamoDBRepository {
        repository.clear()
        repository["id1"] = Category(id = "id1", key = "chicken")

        return mock<CategoryDynamoDBRepository> {
            on { findAll() }.thenReturn(Flux.fromIterable(repository.values.toList()))
        }
    }

    @Nested
    @DisplayName("카테고리 조회")
    inner class FindAllTest {

        @Test
        @DisplayName("카테고리 목록 조회 성공")
        fun testFindAllSuccess() {
            categoryStorageService.findAll()
                .collectList()
                .test()
                .expectNextMatches { it.isNotEmpty() }
                .expectComplete()
        }
    }
}
