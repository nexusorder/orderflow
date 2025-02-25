package com.nexusorder.orderflow.service.domain

import com.nexusorder.orderflow.model.payload.OrderRequest
import com.nexusorder.orderflow.model.storage.Member
import com.nexusorder.orderflow.model.storage.Order
import com.nexusorder.orderflow.model.storage.Shop
import com.nexusorder.orderflow.service.storage.MemberStorageService
import com.nexusorder.orderflow.service.storage.OrderStorageService
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

// 주문 서비스 테스트
class OrderServiceTest {

    private lateinit var orderService: OrderService
    private lateinit var memberStorageService: MemberStorageService
    private lateinit var orderStorageService: OrderStorageService
    private lateinit var shopStorageService: ShopStorageService
    private lateinit var productStorageService: ProductStorageService

    private val repository = mutableMapOf<String, Order>()

    @BeforeEach
    fun setUp() {
        memberStorageService = createMockMemberStorageService()
        orderStorageService = createMockOrderStorageService()
        shopStorageService = createMockShopStorageService()
        productStorageService = mock()
        orderService =
            OrderService(memberStorageService, orderStorageService, shopStorageService, productStorageService)
    }

    private fun createMockMemberStorageService(): MemberStorageService {
        return mock {
            on { findById(any()) }.thenAnswer { invocation ->
                when (invocation.getArgument<String>(0)) {
                    "member1" -> Mono.just(Member(id = "member1", login = "memberId", password = "memberPasswordHash"))
                    else -> Mono.empty()
                }
            }
        }
    }

    private fun createMockShopStorageService(): ShopStorageService {
        return mock {
            on { findById(any()) }.thenAnswer { invocation ->
                when (invocation.getArgument<String>(0)) {
                    "shop1" -> Mono.just(Shop(id = "shop1", name = "Shop 1", category = "Category 1"))
                    else -> Mono.empty()
                }
            }
        }
    }

    private fun createMockOrderStorageService(): OrderStorageService {
        repository.clear()
        repository["order1"] = Order(
            id = "order1",
            memberId = "member1",
            shopId = "shop1",
            products = listOf()
        )

        return mock {
            on { findById(any()) }.thenAnswer { invocation ->
                val id = invocation.getArgument<String>(0)
                Mono.justOrEmpty(repository[id])
            }
            on { findAllByMemberId(any()) }.thenReturn(Flux.fromIterable(repository.values.toList()))
            on { save(any()) }.thenAnswer { invocation ->
                val order = invocation.getArgument<Order>(0)
                Mono.just(order)
            }
        }
    }

    @Nested
    @DisplayName("주문 상세 조회")
    inner class FindByIdAndMemberIdTest {

        @Test
        @DisplayName("존재하는 주문 조회 성공")
        fun testFindByIdAndMemberIdSuccess() {
            orderService.findByIdAndMemberId("order1", "member1")
                .test()
                .expectNextMatches { it.memberId == "member1" }
                .expectComplete()
        }

        @Test
        @DisplayName("존재하지 않는 주문 조회 실패")
        fun testFindByIdAndMemberIdFail() {
            orderService.findByIdAndMemberId("order2", "member1")
                .test()
                .expectComplete()
        }
    }

    @Nested
    @DisplayName("주문 목록 조회")
    inner class FindAllByMemberIdTest {

        @Test
        @DisplayName("주문 목록 조회 성공")
        fun testFindAllByMemberIdSuccess() {
            orderService.findAllByMemberId("member1")
                .test()
                .expectNextMatches { it.isNotEmpty() }
                .expectComplete()
        }
    }

    @Nested
    @DisplayName("주문 생성")
    inner class SaveTest {

        @Test
        @DisplayName("주문 생성 성공")
        fun testSaveSuccess() {
            val request = OrderRequest(shopId = "shop1", products = listOf())
            orderService.save("member1", request)
                .test()
                .expectNextMatches { it.memberId == "member1" }
                .expectComplete()
        }
    }
}
