package com.nexusorder.orderflow.service.storage

import com.nexusorder.orderflow.model.storage.Member
import com.nexusorder.orderflow.repository.dynamodb.MemberDynamoDBRepository
import com.nexusorder.orderflow.service.domain.EncryptionService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import reactor.core.publisher.Flux
import reactor.kotlin.test.test

class MemberStorageServiceTest {

    private lateinit var memberStorageService: MemberStorageService
    private lateinit var memberRepository: MemberDynamoDBRepository
    private lateinit var encryptionService: EncryptionService
    private val repository = mutableMapOf<String, Member>()

    @BeforeEach
    fun setUp() {
        encryptionService = mock()
        memberRepository = createMockMemberRepository()
        memberStorageService = MemberStorageService(memberRepository, encryptionService)
    }

    private fun createMockMemberRepository(): MemberDynamoDBRepository {
        repository.clear()
        repository["id1"] = Member(id = "id1", login = "login1", email = "encryptedEmail")

        return mock<MemberDynamoDBRepository> {
            on { findAll() }.thenReturn(Flux.fromIterable(repository.values.toList()))
        }
    }

    @Nested
    @DisplayName("회원 조회")
    inner class FindAllTest {

        @Test
        @DisplayName("회원 목록 조회 성공")
        fun testFindAllSuccess() {
            memberStorageService.findAll()
                .collectList()
                .test()
                .expectNextMatches { it.isNotEmpty() }
                .expectComplete()
        }
    }
}
