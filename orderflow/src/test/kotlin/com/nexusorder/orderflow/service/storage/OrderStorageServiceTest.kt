package com.nexusorder.orderflow.service.storage

import com.nexusorder.orderflow.model.storage.Order
import com.nexusorder.orderflow.repository.dynamodb.OrderDynamoDBRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import reactor.core.publisher.Flux
import reactor.kotlin.test.test

class OrderStorageServiceTest {

    private lateinit var orderStorageService: OrderStorageService
    private lateinit var orderRepository: OrderDynamoDBRepository
    private val repository = mutableMapOf<String, Order>()

    @BeforeEach
    fun setUp() {
        orderRepository = createMockOrderRepository()
        orderStorageService = OrderStorageService(orderRepository)
    }

    private fun createMockOrderRepository(): OrderDynamoDBRepository {
        repository.clear()
        repository["id1"] = Order(id = "id1", memberId = "member1")

        return mock<OrderDynamoDBRepository> {
            on { findAll() }.thenReturn(Flux.fromIterable(repository.values.toList()))
        }
    }

    @Nested
    @DisplayName("주문 조회")
    inner class FindAllTest {

        @Test
        @DisplayName("주문 목록 조회 성공")
        fun testFindAllSuccess() {
            orderStorageService.findAll()
                .collectList()
                .test()
                .expectNextMatches { it.isNotEmpty() }
                .expectComplete()
        }
    }
}
