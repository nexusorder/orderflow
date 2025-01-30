package com.nexusorder.orderflow.service.domain

import com.nexusorder.orderflow.model.payload.SearchRequest
import com.nexusorder.orderflow.model.storage.Product
import com.nexusorder.orderflow.model.storage.Shop
import com.nexusorder.orderflow.service.storage.ProductStorageService
import com.nexusorder.orderflow.service.storage.ShopStorageService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import reactor.core.publisher.Flux
import reactor.kotlin.test.test

// 검색 서비스 테스트
class SearchServiceTest {

    private lateinit var searchService: SearchService
    private lateinit var shopStorageService: ShopStorageService
    private lateinit var productStorageService: ProductStorageService

    private val repository = mutableMapOf<String, Shop>()

    @BeforeEach
    fun setUp() {
        shopStorageService = createMockShopStorageService()
        productStorageService = createMockProductStorageService()
        searchService = SearchService(shopStorageService, productStorageService)
    }

    private fun createMockShopStorageService(): ShopStorageService {
        repository.clear()
        repository["shop1"] = Shop(id = "shop1", name = "강남 치킨", category = "chicken")

        return mock {
            on { findAll() }.thenReturn(Flux.fromIterable(repository.values.toList()))
        }
    }

    private fun createMockProductStorageService(): ProductStorageService {
        return mock {
            on { findAll() }.thenReturn(
                Flux.fromIterable(
                    listOf(
                        Product(id = "product1", name = "강남 치킨 1", shopId = "shop1"),
                        Product(id = "product2", name = "강남 치킨 2", shopId = "shop1"),
                        Product(id = "product3", name = "강남 치킨 3", shopId = "shop1")
                    )
                )
            )
        }
    }

    @Nested
    @DisplayName("검색")
    inner class SearchTest {

        @Test
        @DisplayName("검색어 중 이름이 포함된 가게 조회 성공")
        fun testSearchSuccessWithName() {
            val request = SearchRequest(name = "치킨", category = "")
            searchService.search(request)
                .test()
                .expectNextMatches { it.isNotEmpty() }
                .expectComplete()
        }

        @Test
        @DisplayName("검색어 중 카테고리에 해당하는 가게 조회 성공")
        fun testSearchSuccessWithCategory() {
            val request = SearchRequest(name = "", category = "chicken")
            searchService.search(request)
                .test()
                .expectNextMatches { it.isNotEmpty() }
                .expectComplete()
        }

        @Test
        @DisplayName("검색어에 해당하는 가게가 없을 경우 빈 리스트 반환")
        fun testSearchFail() {
            val request = SearchRequest(name = "피자", category = "")
            searchService.search(request)
                .test()
                .expectNextMatches { it.isEmpty() }
                .expectComplete()
        }
    }
}
