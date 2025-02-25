package com.nexusorder.orderflow.service.domain

import com.nexusorder.orderflow.model.payload.LoginRequest
import com.nexusorder.orderflow.model.payload.SignupRequest
import com.nexusorder.orderflow.model.storage.Member
import com.nexusorder.orderflow.service.storage.MemberStorageService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import reactor.core.publisher.Mono
import reactor.kotlin.test.test

// 인증 서비스 테스트
class AuthServiceTest {

    // CUT(Class Under Test)
    private lateinit var authService: AuthService

    // Dependencies
    private lateinit var memberStorageService: MemberStorageService

    // Mock repository
    private val repository = mutableMapOf<String, Member>()

    @BeforeEach
    fun setUp() {
        memberStorageService = createMockMemberStorageService()
        authService = AuthService(memberStorageService)
    }

    private fun createMockMemberStorageService(): MemberStorageService {
        repository.clear()
        repository["id1"] = Member(
            id = "id1",
            login = "memberId",
            password = "memberPasswordHash"
        )

        return mock<MemberStorageService> {
            on { findByLogin(any()) }.thenAnswer { invocation ->
                val login = invocation.getArgument<String>(0)
                Mono.justOrEmpty(repository.values.find { it.login == login })
            }
            on { findById(any()) }.thenAnswer { invocation ->
                val id = invocation.getArgument<String>(0)
                Mono.justOrEmpty(repository[id])
            }
            on { existsByLogin(any()) }.thenAnswer { invocation ->
                val login = invocation.getArgument<String>(0)
                Mono.just(repository.values.any { it.login == login })
            }
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
            authService.login(request)
                .test()
                .expectNextMatches {
                    it.login == "memberId" &&
                        it.password == "memberPasswordHash"
                }
                .expectComplete()
        }

        @Test
        @DisplayName("사용자가 존재하고 비밀번호 해시가 일치하지 않으면 로그인 시 빈 값을 반환한다")
        fun testLoginFailWithPassword() {
            // given
            val request = LoginRequest(
                login = "memberId",
                password = "memberPasswordHashWrong"
            )

            // when
            authService.login(request)
                .test()
                .expectComplete()
        }

        @Test
        @DisplayName("사용자가 존재하지 않으면 로그인 시 빈 값을 반환한다")
        fun testLoginFailWithLogin() {
            // given
            val request = LoginRequest(
                login = "memberId2",
                password = "memberPassword2"
            )

            // when
            authService.login(request)
                .test()
                .expectComplete()
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
            authService.signup(request)
                .test()
                .expectNextMatches {
                    it.login == "memberId2" &&
                        it.password == "memberPasswordHash2"
                }
                .expectComplete()
        }

        @Test
        @DisplayName("사용자가 존재하면 회원가입 시 빈 값을 반환한다")
        fun testSignupFail() {
            // given
            val request = SignupRequest(
                login = "memberId",
                password = "memberPasswordHash",
                passwordConfirm = "memberPasswordHash"
            )

            // when
            authService.signup(request)
                .test()
                .expectComplete()
        }
    }

    @Nested
    @DisplayName("프로파일")
    inner class ProfileTest {

        @Test
        @DisplayName("존재하는 사용자 프로필 조회 성공한다")
        fun testProfileSuccess() {
            // when
            authService.profile("id1")
                .test()
                .expectNextMatches {
                    it.login == "memberId"
                }
                .expectComplete()
        }

        @Test
        @DisplayName("존재하지 않는 사용자 프로필 조회 시 빈 값을 반환한다")
        fun testProfileFail() {
            // when
            authService.profile("id2")
                .test()
                .expectComplete()
        }
    }
}
