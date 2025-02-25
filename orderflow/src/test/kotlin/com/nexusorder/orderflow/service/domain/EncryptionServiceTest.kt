package com.nexusorder.orderflow.service.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

// 암호화 서비스 테스트
class EncryptionServiceTest {

    private lateinit var encryptionService: EncryptionService
    private val key = "0ojvNbYkHCWHj18OIqkUIA=="
    private val iv = "Gqe2yh/v4npW3IjTCGW7kg=="

    @BeforeEach
    fun setUp() {
        encryptionService = EncryptionService(key, iv)
    }

    @Nested
    @DisplayName("암호화")
    inner class EncryptTest {

        @Test
        @DisplayName("데이터 암호화 성공")
        fun testEncryptSuccess() {
            val data = "testData"
            val actual = encryptionService.encrypt(data)
            val expected = "o/JsiXgjzIMzgJSpHQT/Ww=="
            assertThat(actual).isEqualTo(expected)
        }
    }

    @Nested
    @DisplayName("복호화")
    inner class DecryptTest {

        @Test
        @DisplayName("데이터 복호화 성공")
        fun testDecryptSuccess() {
            val data = "o/JsiXgjzIMzgJSpHQT/Ww=="
            val actual = encryptionService.decrypt(data)
            val expected = "testData"
            assertThat(actual).isEqualTo(expected)
        }
    }
}
