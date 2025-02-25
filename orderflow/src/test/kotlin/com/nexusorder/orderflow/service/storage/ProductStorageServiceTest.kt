package com.nexusorder.orderflow.service.storage

import com.nexusorder.orderflow.model.storage.Product
import com.nexusorder.orderflow.repository.dynamodb.ProductDynamoDBRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import reactor.core.publisher.Flux
import reactor.kotlin.test.test

class ProductStorageServiceTest {

    private lateinit var productStorageService: ProductStorageService
    private lateinit var productRepository: ProductDynamoDBRepository
    private val repository = mutableMapOf<String, Product>()

    @BeforeEach
    fun setUp() {
        productRepository = createMockProductRepository()
        productStorageService = ProductStorageService(productRepository)
    }

    private fun createMockProductRepository(): ProductDynamoDBRepository {
        repository.clear()
        repository["id1"] = Product(id = "id1", name = "Product1", shopId = "shop1")

        return mock<ProductDynamoDBRepository> {
            on { findAll() }.thenReturn(Flux.fromIterable(repository.values.toList()))
        }
    }

    @Nested
    @DisplayName("상품 조회")
    inner class FindAllTest {

        @Test
        @DisplayName("상품 목록 조회 성공")
        fun testFindAllSuccess() {
            productStorageService.findAll()
                .collectList()
                .test()
                .expectNextMatches { it.isNotEmpty() }
                .expectComplete()
        }
    }
}
