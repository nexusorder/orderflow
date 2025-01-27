package com.nexusorder.orderflow.service.storage

import com.nexusorder.orderflow.model.storage.Shop
import com.nexusorder.orderflow.repository.dynamodb.ShopDynamoDBRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import reactor.core.publisher.Flux
import reactor.kotlin.test.test

class ShopStorageServiceTest {

    private lateinit var shopStorageService: ShopStorageService
    private lateinit var shopRepository: ShopDynamoDBRepository
    private val repository = mutableMapOf<String, Shop>()

    @BeforeEach
    fun setUp() {
        shopRepository = createMockShopRepository()
        shopStorageService = ShopStorageService(shopRepository)
    }

    private fun createMockShopRepository(): ShopDynamoDBRepository {
        repository.clear()
        repository["id1"] = Shop(id = "id1", name = "Shop1")

        return mock<ShopDynamoDBRepository> {
            on { findAll() }.thenReturn(Flux.fromIterable(repository.values.toList()))
        }
    }

    @Nested
    @DisplayName("가게 조회")
    inner class FindAllTest {

        @Test
        @DisplayName("가게 목록 조회 성공")
        fun testFindAllSuccess() {
            shopStorageService.findAll()
                .collectList()
                .test()
                .expectNextMatches { it.isNotEmpty() }
                .expectComplete()
        }
    }
}
