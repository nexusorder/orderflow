package com.nexusorder.orderflow.integration

import com.nexusorder.orderflow.model.payload.ApiResponse
import com.nexusorder.orderflow.model.storage.Category
import com.nexusorder.orderflow.repository.dynamodb.CategoryDynamoDBRepository
import com.nexusorder.orderflow.repository.dynamodb.MemberDynamoDBRepository
import com.nexusorder.orderflow.repository.dynamodb.OrderDynamoDBRepository
import com.nexusorder.orderflow.repository.dynamodb.ProductDynamoDBRepository
import com.nexusorder.orderflow.repository.dynamodb.ReviewDynamoDBRepository
import com.nexusorder.orderflow.repository.dynamodb.ShopDynamoDBRepository
import com.nexusorder.orderflow.service.domain.CategoryService
import com.nexusorder.orderflow.service.storage.CategoryStorageService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.given
import org.mockito.kotlin.willAnswer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.mock.mockito.MockBeans
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux

// 카테고리 통합 테스트
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
class CategoryIntegrationTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockBean
    private lateinit var categoryStorageService: CategoryStorageService

    @Autowired
    private lateinit var categoryService: CategoryService

    private val repository = mutableMapOf<String, Category>()

    @BeforeEach
    fun setUp() {
        mockCategoryStorageService()
    }

    private fun mockCategoryStorageService() {
        repository.clear()
        repository["id1"] = Category(id = "id1", key = "chicken")

        given { categoryStorageService.findAll() } willAnswer {
            Flux.fromIterable(repository.values.toList())
        }
    }

    @Nested
    @DisplayName("카테고리 조회")
    inner class FindAllTest {

        @Test
        @DisplayName("카테고리 목록 조회 성공")
        fun testFindAllSuccess() {
            webTestClient.get()
                .uri("/api/v1/categories")
                .exchange()
                .expectStatus().isOk
                .expectBody(ApiResponse::class.java)
                .consumeWith { response ->
                    val body = response.responseBody
                    assertThat(body).isNotNull
                    assertThat(body?.success).isTrue()
                    assertThat(body?.data).isNotNull
                    assertThat((body?.data as List<*>).isNotEmpty()).isTrue()
                }
        }
    }
}
