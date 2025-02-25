package com.nexusorder.orderflow.service.domain

import com.nexusorder.orderflow.model.payload.ProductRequest
import com.nexusorder.orderflow.model.storage.Product
import com.nexusorder.orderflow.service.storage.ProductStorageService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.test.test

// 상품 서비스 테스트
class ProductServiceTest {

    private lateinit var productService: ProductService
    private lateinit var productStorageService: ProductStorageService

    private val repository = mutableMapOf<String, Product>()

    @BeforeEach
    fun setUp() {
        productStorageService = createMockProductStorageService()
        productService = ProductService(productStorageService)
    }

    private fun createMockProductStorageService(): ProductStorageService {
        repository.clear()
        repository["product1"] = Product(id = "product1", name = "Product 1", shopId = "shop1")

        return mock {
            on { findById(any()) }.thenAnswer { invocation ->
                val id = invocation.getArgument<String>(0)
                Mono.justOrEmpty(repository[id])
            }
            on { findAll() }.thenReturn(Flux.fromIterable(repository.values.toList()))
            on { save(any()) }.thenAnswer { invocation ->
                val product = invocation.getArgument<Product>(0)
                repository[product.id] = product
                Mono.just(product)
            }
        }
    }

    @Nested
    @DisplayName("상품 상세 조회")
    inner class FindByIdTest {

        @Test
        @DisplayName("존재하는 상품 조회 성공")
        fun testFindByIdSuccess() {
            productService.findById("product1")
                .test()
                .expectNextMatches { it.name == "Product 1" }
                .expectComplete()
        }

        @Test
        @DisplayName("존재하지 않는 상품 조회 실패")
        fun testFindByIdFail() {
            productService.findById("product2")
                .test()
                .expectComplete()
        }
    }

    @Nested
    @DisplayName("상품 목록 조회")
    inner class FindAllTest {

        @Test
        @DisplayName("상품 목록 조회 성공")
        fun testFindAllSuccess() {
            productService.findAll()
                .test()
                .expectNextMatches { it.isNotEmpty() }
                .expectComplete()
        }
    }

    @Nested
    @DisplayName("상품 생성")
    inner class SaveTest {

        @Test
        @DisplayName("상품 생성 성공")
        fun testSaveSuccess() {
            val request = ProductRequest(name = "Product 2", shopId = "shop1")
            productService.save(request)
                .test()
                .expectNextMatches { it.name == "Product 2" }
                .expectComplete()
        }
    }
}
