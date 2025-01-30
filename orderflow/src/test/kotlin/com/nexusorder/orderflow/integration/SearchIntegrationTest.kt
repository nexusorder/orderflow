package com.nexusorder.orderflow.integration

import com.nexusorder.orderflow.model.payload.ApiResponse
import com.nexusorder.orderflow.model.payload.SearchRequest
import com.nexusorder.orderflow.model.storage.Product
import com.nexusorder.orderflow.model.storage.Shop
import com.nexusorder.orderflow.repository.dynamodb.CategoryDynamoDBRepository
import com.nexusorder.orderflow.repository.dynamodb.MemberDynamoDBRepository
import com.nexusorder.orderflow.repository.dynamodb.OrderDynamoDBRepository
import com.nexusorder.orderflow.repository.dynamodb.ProductDynamoDBRepository
import com.nexusorder.orderflow.repository.dynamodb.ReviewDynamoDBRepository
import com.nexusorder.orderflow.repository.dynamodb.ShopDynamoDBRepository
import com.nexusorder.orderflow.service.domain.SearchService
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

// 검색 통합 테스트
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
class SearchIntegrationTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockBean
    private lateinit var shopStorageService: ShopStorageService

    @MockBean
    private lateinit var productStorageService: ProductStorageService

    @Autowired
    private lateinit var searchService: SearchService

    private val shopRepository = mutableMapOf<String, Shop>()
    private val productRepository = mutableMapOf<String, Product>()

    @BeforeEach
    fun setUp() {
        mockStorageServices()
    }

    private fun mockStorageServices() {
        shopRepository.clear()
        productRepository.clear()
        shopRepository["shop1"] = Shop(id = "shop1", name = "강남 치킨", category = "chicken")
        productRepository["product1"] = Product(id = "product1", name = "강남 치킨 1", shopId = "shop1")

        given { shopStorageService.findAll() } willAnswer {
            Flux.fromIterable(shopRepository.values.toList())
        }
        given { shopStorageService.findById(any()) } willAnswer { invocation ->
            val id = invocation.getArgument<String>(0)
            Mono.justOrEmpty(shopRepository[id])
        }
        given { productStorageService.findAll() } willAnswer {
            Flux.fromIterable(productRepository.values.toList())
        }
        given { productStorageService.findAllByShopId(any()) } willAnswer { invocation ->
            val shopId = invocation.getArgument<String>(0)
            Flux.fromIterable(productRepository.values.filter { it.shopId == shopId })
        }
    }

    @Nested
    @DisplayName("검색")
    inner class SearchTest {

        @Test
        @DisplayName("검색어 중 이름이 포함된 가게 조회 성공")
        fun testSearchSuccessWithName() {
            val request = SearchRequest(name = "치킨", category = "")
            webTestClient.post()
                .uri("/api/v1/search")
                .body(Mono.just(request), SearchRequest::class.java)
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
