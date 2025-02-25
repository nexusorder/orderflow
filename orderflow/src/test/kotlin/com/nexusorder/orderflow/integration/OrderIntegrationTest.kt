package com.nexusorder.orderflow.integration

import com.nexusorder.orderflow.model.payload.ApiResponse
import com.nexusorder.orderflow.model.payload.OrderRequest
import com.nexusorder.orderflow.model.storage.Member
import com.nexusorder.orderflow.model.storage.Order
import com.nexusorder.orderflow.model.storage.Product
import com.nexusorder.orderflow.model.storage.Shop
import com.nexusorder.orderflow.repository.dynamodb.CategoryDynamoDBRepository
import com.nexusorder.orderflow.repository.dynamodb.MemberDynamoDBRepository
import com.nexusorder.orderflow.repository.dynamodb.OrderDynamoDBRepository
import com.nexusorder.orderflow.repository.dynamodb.ProductDynamoDBRepository
import com.nexusorder.orderflow.repository.dynamodb.ReviewDynamoDBRepository
import com.nexusorder.orderflow.repository.dynamodb.ShopDynamoDBRepository
import com.nexusorder.orderflow.service.domain.AuthService
import com.nexusorder.orderflow.service.domain.OrderService
import com.nexusorder.orderflow.service.storage.MemberStorageService
import com.nexusorder.orderflow.service.storage.OrderStorageService
import com.nexusorder.orderflow.service.storage.ProductStorageService
import com.nexusorder.orderflow.service.storage.ShopStorageService
import com.nexusorder.orderflow.support.SessionMutator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.mockito.kotlin.willAnswer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.mock.mockito.MockBeans
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

// 주문 통합 테스트
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@MockBeans(
    MockBean(CategoryDynamoDBRepository::class),
    MockBean(MemberDynamoDBRepository::class),
    MockBean(OrderDynamoDBRepository::class),
    MockBean(ProductDynamoDBRepository::class),
    MockBean(ReviewDynamoDBRepository::class),
    MockBean(ShopDynamoDBRepository::class)
)
@AutoConfigureWebTestClient
class OrderIntegrationTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockBean
    private lateinit var memberStorageService: MemberStorageService

    @MockBean
    private lateinit var orderStorageService: OrderStorageService

    @MockBean
    private lateinit var shopStorageService: ShopStorageService

    @MockBean
    private lateinit var productStorageService: ProductStorageService

    @Autowired
    private lateinit var orderService: OrderService

    private val repository = mutableMapOf<String, Order>()

    @BeforeEach
    fun setUp() {
        mockMemberStorageService()
        mockOrderStorageService()
        mockShopStorageService()
        mockProductStorageService()
    }

    private fun mockMemberStorageService() {
        given { memberStorageService.findById(any()) } willAnswer { invocation ->
            val id = invocation.getArgument<String>(0)
            Mono.justOrEmpty(
                when (id) {
                    "member1" -> Member(id = "member1", login = "memberId", password = "memberPasswordHash")
                    else -> null
                }
            )
        }
    }

    private fun mockOrderStorageService() {
        repository.clear()
        repository["order1"] = Order(id = "order1", memberId = "member1", shopId = "shop1", products = listOf())

        given { orderStorageService.findById(any()) } willAnswer { invocation ->
            val id = invocation.getArgument<String>(0)
            Mono.justOrEmpty(repository[id])
        }
        given { orderStorageService.findAllByMemberId(any()) } willAnswer { invocation ->
            val memberId = invocation.getArgument<String>(0)
            Flux.fromIterable(repository.values.filter { it.memberId == memberId })
        }
        given { orderStorageService.save(any()) } willAnswer { invocation ->
            val order = invocation.getArgument<Order>(0)
            repository[order.id] = order
            Mono.just(order)
        }
    }

    private fun mockShopStorageService() {
        given { shopStorageService.findById(any()) } willAnswer { invocation ->
            val id = invocation.getArgument<String>(0)
            Mono.justOrEmpty(
                when (id) {
                    "shop1" -> Shop(id = "shop1", name = "shop1", owner = "owner1")
                    else -> null
                }
            )
        }
        given { shopStorageService.findAll() } willAnswer {
            Flux.just(
                Shop(id = "shop1", name = "shop1", owner = "owner1"),
                Shop(id = "shop2", name = "shop2", owner = "owner2"),
                Shop(id = "shop3", name = "shop3", owner = "owner3"),
            )
        }
    }

    private fun mockProductStorageService() {
        given { productStorageService.findAll() } willAnswer {
            Flux.just(
                Product(id = "product1", shopId = "shop1", name = "product1", price = 1000),
                Product(id = "product2", shopId = "shop1", name = "product2", price = 2000),
                Product(id = "product3", shopId = "shop1", name = "product3", price = 3000),
            )
        }
    }

    @Nested
    @DisplayName("주문 조회")
    inner class FindTest {

        @Test
        @DisplayName("주문 조회 성공")
        fun testFindSuccess() {
            webTestClient.mutateWith(
                SessionMutator(mapOf(AuthService.LOGIN_KEY to "member1"))
            )
                .get()
                .uri("/api/v1/orders/order1")
                .exchange()
                .expectStatus().isOk
                .expectBody(ApiResponse::class.java)
                .consumeWith { response ->
                    val body = response.responseBody
                    assertThat(body).isNotNull
                    assertThat(body?.success).isTrue()
                    assertThat(body?.data).isNotNull
                }
        }

        @Test
        @DisplayName("주문 조회 실패 (존재하지 않는 주문)")
        fun testFindFail() {
            webTestClient.mutateWith(
                SessionMutator(mapOf(AuthService.LOGIN_KEY to "member1"))
            )
                .get()
                .uri("/api/v1/orders/order2")
                .exchange()
                .expectStatus().isNotFound
                .expectBody(ApiResponse::class.java)
                .consumeWith { response ->
                    val body = response.responseBody
                    assertThat(body).isNotNull
                    assertThat(body?.success).isFalse()
                    assertThat(body?.data).isNull()
                }
        }

        @Test
        @DisplayName("주문 조회 실패 (인증 실패)")
        fun testFindFailByUnauthorized() {
            webTestClient
                .get()
                .uri("/api/v1/orders/order1")
                .exchange()
                .expectStatus().isUnauthorized
                .expectBody(ApiResponse::class.java)
                .consumeWith { response ->
                    val body = response.responseBody
                    assertThat(body).isNotNull
                    assertThat(body?.success).isFalse()
                    assertThat(body?.data).isNull()
                }
        }

        @Test
        @DisplayName("주문 목록 조회 성공")
        fun testFindAllSuccess() {
            webTestClient.mutateWith(
                SessionMutator(mapOf(AuthService.LOGIN_KEY to "member1"))
            )
                .get()
                .uri("/api/v1/orders")
                .exchange()
                .expectStatus().isOk
                .expectBody(ApiResponse::class.java)
                .consumeWith { response ->
                    val body = response.responseBody
                    assertThat(body).isNotNull
                    assertThat(body?.success).isTrue()
                    assertThat(body?.data).isNotNull
                }
        }

        @Test
        @DisplayName("주문 목록 조회 실패 (인증 실패)")
        fun testFindAllFailByUnauthorized() {
            webTestClient
                .get()
                .uri("/api/v1/orders")
                .exchange()
                .expectStatus().isUnauthorized
                .expectBody(ApiResponse::class.java)
                .consumeWith { response ->
                    val body = response.responseBody
                    assertThat(body).isNotNull
                    assertThat(body?.success).isFalse()
                    assertThat(body?.data).isNull()
                }
        }
    }

    @Nested
    @DisplayName("주문 생성")
    inner class SaveTest {

        @Test
        @DisplayName("주문 생성 성공")
        fun testSaveSuccess() {
            val request = OrderRequest(shopId = "shop1", products = listOf())
            webTestClient.mutateWith(
                SessionMutator(mapOf(AuthService.LOGIN_KEY to "member1"))
            )
                .post()
                .uri("/api/v1/orders")
                .body(Mono.just(request), OrderRequest::class.java)
                .exchange()
                .expectStatus().isOk
                .expectBody(ApiResponse::class.java)
                .consumeWith { response ->
                    val body = response.responseBody
                    assertThat(body).isNotNull
                    assertThat(body?.success).isTrue()
                    assertThat(body?.data).isNotNull
                }
        }
    }
}
