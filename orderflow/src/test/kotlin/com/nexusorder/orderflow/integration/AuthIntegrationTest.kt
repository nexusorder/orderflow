package com.nexusorder.orderflow.integration

import com.nexusorder.orderflow.model.payload.ApiResponse
import com.nexusorder.orderflow.model.payload.LoginRequest
import com.nexusorder.orderflow.model.payload.SignupRequest
import com.nexusorder.orderflow.model.storage.Member
import com.nexusorder.orderflow.repository.dynamodb.CategoryDynamoDBRepository
import com.nexusorder.orderflow.repository.dynamodb.MemberDynamoDBRepository
import com.nexusorder.orderflow.repository.dynamodb.OrderDynamoDBRepository
import com.nexusorder.orderflow.repository.dynamodb.ProductDynamoDBRepository
import com.nexusorder.orderflow.repository.dynamodb.ReviewDynamoDBRepository
import com.nexusorder.orderflow.repository.dynamodb.ShopDynamoDBRepository
import com.nexusorder.orderflow.service.domain.AuthService
import com.nexusorder.orderflow.service.storage.MemberStorageService
import com.nexusorder.orderflow.support.SessionMutator
import com.nexusorder.orderflow.util.CoreLogger
import com.nexusorder.orderflow.util.HashUtil
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

// 인증 통합 테스트
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@MockBeans(
    MockBean(CategoryDynamoDBRepository::class),
    MockBean(MemberDynamoDBRepository::class),
    MockBean(OrderDynamoDBRepository::class),
    MockBean(ProductDynamoDBRepository::class),
    MockBean(ReviewDynamoDBRepository::class),
    MockBean(ShopDynamoDBRepository::class)
)
@AutoConfigureWebTestClient
class AuthIntegrationTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    // Dependency Injection
    @MockBean
    private lateinit var memberStorageService: MemberStorageService

    @Autowired
    private lateinit var authService: AuthService

    // Mock repository
    private val repository = mutableMapOf<String, Member>()

    @BeforeEach
    fun setUp() {
        mockMemberStorageService()
    }

    private fun mockMemberStorageService() {
        repository.clear()
        repository["id1"] = Member(
            id = "id1",
            login = "memberId",
            password = HashUtil.getPasswordHash("memberPasswordHash")
        )

        given {
            memberStorageService.findByLogin(any())
        } willAnswer { invocation ->
            val login = invocation.getArgument<String>(0)
            Mono.justOrEmpty(repository.values.find { it.login == login })
        }
        given {
            memberStorageService.findById(any())
        } willAnswer { invocation ->
            CoreLogger.info("!".repeat(50), message = "invocation: $invocation")
            val id = invocation.getArgument<String>(0)
            Mono.justOrEmpty(repository[id])
        }
        given {
            memberStorageService.existsByLogin(any())
        } willAnswer { invocation ->
            val login = invocation.getArgument<String>(0)
            Mono.just(repository.values.any { it.login == login })
        }
        given {
            memberStorageService.save(any())
        } willAnswer { invocation ->
            val member = invocation.getArgument<Member>(0)
            repository[member.id] = member
            Mono.just(member)
        }
    }

    @Nested
    @DisplayName("로그인")
    inner class LoginTest {

        @Test
        @DisplayName("사용자가 존재하고 비밀번호 해시가 일치하면 로그인 성공한다")
        fun testLoginSuccess() {
            // given
            val request = LoginRequest(
                login = "memberId",
                password = "memberPasswordHash"
            )

            // when
            webTestClient.post()
                .uri("/api/v1/auth/login")
                .body(Mono.just(request), LoginRequest::class.java).exchange()
                // then
                .expectStatus().isOk
                .expectBody(ApiResponse::class.java)
                .consumeWith { response ->
                    val body = response.responseBody
                    assertThat(body).isNotNull
                    assertThat(body?.success).isTrue()
                    assertThat(body?.data).isEqualTo(true)
                }
        }

        @Test
        @DisplayName("사용자가 존재하고 비밀번호 해시가 일치하지 않으면 로그인 실패한다")
        fun testLoginFailWithPassword() {
            // given
            val request = LoginRequest(
                login = "memberId",
                password = "memberPasswordHashWrong"
            )

            // when
            webTestClient.post()
                .uri("/api/v1/auth/login")
                .body(Mono.just(request), LoginRequest::class.java).exchange()
                // then
                .expectStatus().isOk
                .expectBody(ApiResponse::class.java)
                .consumeWith { response ->
                    val body = response.responseBody
                    assertThat(body).isNotNull
                    assertThat(body?.success).isTrue()
                    assertThat(body?.data).isEqualTo(false)
                }
        }

        @Test
        @DisplayName("사용자가 존재하지 않으면 로그인 실패한다")
        fun testLoginFailWithLogin() {
            // given
            val request = LoginRequest(
                login = "memberId2",
                password = "memberPassword2"
            )

            // when
            webTestClient.post().uri("/api/v1/auth/login")
                .body(Mono.just(request), LoginRequest::class.java)
                .exchange()
                // then
                .expectStatus().isOk
                .expectBody(ApiResponse::class.java)
                .consumeWith { response ->
                    val body = response.responseBody
                    assertThat(body).isNotNull
                    assertThat(body?.success).isTrue()
                    assertThat(body?.data).isEqualTo(false)
                }
        }
    }

    @Nested
    @DisplayName("회원가입")
    inner class SignupTest {

        @Test
        @DisplayName("사용자가 존재하지 않으면 회원가입 성공한다")
        fun testSignupSuccess() {
            // given
            val request = SignupRequest(
                login = "memberId2",
                password = "memberPasswordHash2",
                passwordConfirm = "memberPasswordHash2"
            )

            // when
            webTestClient.post().uri("/api/v1/auth/signup")
                .body(Mono.just(request), SignupRequest::class.java)
                .exchange()
                // then
                .expectStatus().isOk
                .expectBody(ApiResponse::class.java)
                .consumeWith { response ->
                    val body = response.responseBody
                    assertThat(body).isNotNull
                    assertThat(body?.success).isTrue()
                    assertThat(body?.data).isEqualTo(true)
                }
        }

        @Test
        @DisplayName("사용자가 존재하면 회원가입 실패한다")
        fun testSignupFail() {
            // given
            val request = SignupRequest(
                login = "memberId",
                password = "memberPasswordHash",
                passwordConfirm = "memberPasswordHash"
            )

            // when
            webTestClient.post().uri("/api/v1/auth/signup").body(Mono.just(request), SignupRequest::class.java)
                .exchange()
                // then
                .expectStatus().isOk
                .expectBody(ApiResponse::class.java)
                .consumeWith { response ->
                    val body = response.responseBody
                    assertThat(body).isNotNull
                    assertThat(body?.success).isTrue()
                    assertThat(body?.data).isEqualTo(false)
                }
        }
    }

    @Nested
    @DisplayName("프로파일")
    inner class ProfileTest {

        @Test
        @DisplayName("로그인된 사용자 프로필 조회 성공한다")
        fun testProfileSuccess() {
            // when
            webTestClient.mutateWith(
                SessionMutator(mapOf(AuthService.LOGIN_KEY to "id1"))
            )
                .get()
                .uri("/api/v1/auth/profile")
                .attribute(AuthService.LOGIN_KEY, "id1")
                .attribute("test", "test")
                .attributes {
                    it["test2"] = "test2"
                }
                .exchange()
                // then
                .expectStatus().isOk
                .expectBody(ApiResponse::class.java)
                .consumeWith { response ->
                    val body = response.responseBody
                    assertThat(body).isNotNull
                    assertThat(body?.success).isTrue()
                    assertThat(body?.data).isNotNull
                    assertThat((body?.data as Map<*, *>)["login"]).isEqualTo("memberId")
                }
        }

        @Test
        @DisplayName("로그인 되지 않은 사용자 프로필 조회 시 null로 응답한다")
        fun testProfileFail() {
            // when
            webTestClient.get()
                .uri("/api/v1/auth/profile")
                .exchange()
                // then
                .expectStatus().isOk
                .expectBody(ApiResponse::class.java)
                .consumeWith { response ->
                    val body = response.responseBody
                    assertThat(body).isNotNull
                    assertThat(body?.success).isTrue()
                    assertThat(body?.data).isEqualTo(null)
                }
        }
    }
}
