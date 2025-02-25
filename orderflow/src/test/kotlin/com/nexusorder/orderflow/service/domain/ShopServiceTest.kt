package com.nexusorder.orderflow.service.domain

import com.nexusorder.orderflow.model.payload.ShopRequest
import com.nexusorder.orderflow.model.storage.Shop
import com.nexusorder.orderflow.service.storage.ProductStorageService
import com.nexusorder.orderflow.service.storage.ShopStorageService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.test.test

// 가게 서비스 테스트
class ShopServiceTest {

    private lateinit var shopService: ShopService
    private lateinit var shopStorageService: ShopStorageService
    private lateinit var productStorageService: ProductStorageService

    private val repository = mutableMapOf<String, Shop>()

    @BeforeEach
    fun setUp() {
        shopStorageService = createMockShopStorageService()
        productStorageService = mock()
        shopService = ShopService(shopStorageService, productStorageService)
    }

    private fun createMockShopStorageService(): ShopStorageService {
        repository.clear()
        repository["shop1"] = Shop(id = "shop1", name = "Shop 1", category = "Category 1")

        return mock {
            on { findById(any()) }.thenAnswer { invocation ->
                val id = invocation.getArgument<String>(0)
                Mono.justOrEmpty(repository[id])
            }
            on { findAll() }.thenReturn(Flux.fromIterable(repository.values.toList()))
            on { save(any()) }.thenAnswer { invocation ->
                val shop = invocation.getArgument<Shop>(0)
                repository[shop.id] = shop
                Mono.just(shop)
            }
        }
    }

    @Nested
    @DisplayName("가게 상세 조회")
    inner class FindByIdTest {

        @Test
        @DisplayName("존재하는 가게 조회 성공")
        fun testFindByIdSuccess() {
            shopService.findById("shop1")
                .test()
                .expectNextMatches { it.name == "Shop 1" }
                .expectComplete()
        }

        @Test
        @DisplayName("존재하지 않는 가게 조회 실패")
        fun testFindByIdFail() {
            shopService.findById("shop2")
                .test()
                .expectComplete()
        }
    }

    @Nested
    @DisplayName("가게 목록 조회")
    inner class FindAllTest {

        @Test
        @DisplayName("가게 목록 조회 성공")
        fun testFindAllSuccess() {
            shopService.findAll()
                .test()
                .expectNextMatches { it.isNotEmpty() }
                .expectComplete()
        }
    }

    @Nested
    @DisplayName("가게 생성")
    inner class SaveTest {

        @Test
        @DisplayName("가게 생성 성공")
        fun testSaveSuccess() {
            val request = ShopRequest(name = "Shop 2", category = "Category 2")
            shopService.save(request)
                .test()
                .expectNextMatches { it.name == "Shop 2" }
                .expectComplete()
        }
    }
}
