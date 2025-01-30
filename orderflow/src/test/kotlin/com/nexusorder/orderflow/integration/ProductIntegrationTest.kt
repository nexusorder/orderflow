
package com.nexusorder.orderflow.integration

import com.nexusorder.orderflow.model.payload.ApiResponse
import com.nexusorder.orderflow.model.payload.ProductRequest
import com.nexusorder.orderflow.model.storage.Product
import com.nexusorder.orderflow.repository.dynamodb.CategoryDynamoDBRepository
import com.nexusorder.orderflow.repository.dynamodb.MemberDynamoDBRepository
import com.nexusorder.orderflow.repository.dynamodb.OrderDynamoDBRepository
import com.nexusorder.orderflow.repository.dynamodb.ProductDynamoDBRepository
import com.nexusorder.orderflow.repository.dynamodb.ReviewDynamoDBRepository
import com.nexusorder.orderflow.repository.dynamodb.ShopDynamoDBRepository
import com.nexusorder.orderflow.service.domain.ProductService
import com.nexusorder.orderflow.service.storage.ProductStorageService
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
import reactor.core.publisher.Mono

// 상품 통합 테스트
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
class ProductIntegrationTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockBean
    private lateinit var productStorageService: ProductStorageService

    @Autowired
    private lateinit var productService: ProductService

    private val repository = mutableMapOf<String, Product>()

    @BeforeEach
    fun setUp() {
        mockProductStorageService()
    }

    private fun mockProductStorageService() {
        repository.clear()
        repository["product1"] = Product(id = "product1", name = "Product 1", shopId = "shop1")

        given { productStorageService.findById(any()) } willAnswer { invocation ->
            val id = invocation.getArgument<String>(0)
            Mono.justOrEmpty(repository[id])
        }
        given { productStorageService.save(any()) } willAnswer { invocation ->
            val product = invocation.getArgument<Product>(0)
            repository[product.id] = product
            Mono.just(product)
        }
    }

    @Nested
    @DisplayName("상품 생성")
    inner class SaveTest {

        @Test
        @DisplayName("상품 생성 성공")
        fun testSaveSuccess() {
            val request = ProductRequest(name = "Product 2", shopId = "shop1")
            webTestClient.post().uri("/api/v1/products")
                .body(Mono.just(request), ProductRequest::class.java)
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
