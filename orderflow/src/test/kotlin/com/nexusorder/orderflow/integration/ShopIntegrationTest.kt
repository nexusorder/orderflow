package com.nexusorder.orderflow.integration

import com.nexusorder.orderflow.model.payload.ApiResponse
import com.nexusorder.orderflow.model.payload.ShopRequest
import com.nexusorder.orderflow.model.storage.Product
import com.nexusorder.orderflow.model.storage.Shop
import com.nexusorder.orderflow.repository.dynamodb.CategoryDynamoDBRepository
import com.nexusorder.orderflow.repository.dynamodb.MemberDynamoDBRepository
import com.nexusorder.orderflow.repository.dynamodb.OrderDynamoDBRepository
import com.nexusorder.orderflow.repository.dynamodb.ProductDynamoDBRepository
import com.nexusorder.orderflow.repository.dynamodb.ReviewDynamoDBRepository
import com.nexusorder.orderflow.repository.dynamodb.ShopDynamoDBRepository
import com.nexusorder.orderflow.service.domain.ShopService
import com.nexusorder.orderflow.service.storage.ProductStorageService
import com.nexusorder.orderflow.service.storage.ShopStorageService
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

// 가게 통합 테스트
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
class ShopIntegrationTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockBean
    private lateinit var shopStorageService: ShopStorageService

    @MockBean
    private lateinit var productStorageService: ProductStorageService

    @Autowired
    private lateinit var shopService: ShopService

    private val repository = mutableMapOf<String, Shop>()

    @BeforeEach
    fun setUp() {
        mockShopStorageService()
        mockProductStorageService()
    }

    private fun mockShopStorageService() {
        repository.clear()
        repository["shop1"] = Shop(id = "shop1", name = "Shop 1", category = "Category 1")

        given { shopStorageService.findById(any()) } willAnswer { invocation ->
            val id = invocation.getArgument<String>(0)
            Mono.justOrEmpty(repository[id])
        }
        given { shopStorageService.findAll() } willAnswer {
            Flux.fromIterable(repository.values.toList())
        }
        given { shopStorageService.save(any()) } willAnswer { invocation ->
            val shop = invocation.getArgument<Shop>(0)
            repository[shop.id] = shop
            Mono.just(shop)
        }
    }

    private fun mockProductStorageService() {
        given { productStorageService.findAllByShopId(any()) } willAnswer { invocation ->
            val shopId = invocation.getArgument<String>(0)
            Flux.just(
                Product(id = "product1", name = "Product 1", shopId = shopId),
                Product(id = "product2", name = "Product 2", shopId = shopId),
                Product(id = "product3", name = "Product 3", shopId = shopId)
            )
        }
    }

    @Nested
    @DisplayName("가게 조회")
    inner class FindAllTest {

        @Test
        @DisplayName("가게 목록 조회 성공")
        fun testFindAllSuccess() {
            webTestClient.get()
                .uri("/api/v1/shops")
                .exchange()
                .expectStatus().isOk
                .expectBody(ApiResponse::class.java)
                .consumeWith { response ->
                    val body = response.responseBody
                    assertThat(body).isNotNull
                    assertThat(body?.success).isTrue()
                    assertThat(body?.data).isNotNull
                    assertThat(body?.data is List<*>).isTrue()
                    assertThat((body?.data as List<*>).isNotEmpty()).isTrue()
                }
        }

        @Test
        @DisplayName("가게 상세 조회 성공")
        fun testFindByIdSuccess() {
            webTestClient.get()
                .uri("/api/v1/shops/shop1")
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

    @Nested
    @DisplayName("가게 생성")
    inner class SaveTest {

        @Test
        @DisplayName("가게 생성 성공")
        fun testSaveSuccess() {
            val request = ShopRequest(name = "Shop 2", category = "Category 2")
            webTestClient.post().uri("/api/v1/shops")
                .body(Mono.just(request), ShopRequest::class.java)
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
