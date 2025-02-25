package com.nexusorder.orderflow.service.domain

import com.nexusorder.orderflow.model.storage.Category
import com.nexusorder.orderflow.service.storage.CategoryStorageService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import reactor.core.publisher.Flux
import reactor.kotlin.test.test

// 카테고리 서비스 테스트
class CategoryServiceTest {

    // CUT(Class Under Test)
    private lateinit var categoryService: CategoryService

    // Dependencies
    private lateinit var categoryStorageService: CategoryStorageService

    // Mock repository
    private val repository = mutableMapOf<String, Category>()

    @BeforeEach
    fun setUp() {
        categoryStorageService = createMockCategoryStorageService()
        categoryService = CategoryService(categoryStorageService)
    }

    private fun createMockCategoryStorageService(): CategoryStorageService {
        repository.clear()
        repository["id1"] = Category(
            id = "id1",
            key = "chicken"
        )

        return mock<CategoryStorageService> {
            on { findAll() }.thenReturn(
                Flux.fromIterable(repository.values.toList())
            )
        }
    }

    @Nested
    @DisplayName("카테고리 조회")
    inner class FindAllTest {

        @Test
        @DisplayName("카테고리 목록 조회 성공")
        fun testFindAllSuccess() {
            categoryService.findAll()
                .test()
                .expectNextMatches { it.isNotEmpty() }
                .expectComplete()
        }
    }
}
