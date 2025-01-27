package com.nexusorder.orderflow.util

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class HashUtilTest {

    private val salt = "\$2a\$10\$aeFBix9YBK47MfjjvK7PHO"

    @Test
    fun testGetHash() {
        val hash = HashUtil.getHash("password", salt)
        assertThat(hash).isNotEmpty()
    }

    @Test
    fun testGetPasswordHash() {
        val passwordHash = HashUtil.getPasswordHash("password")
        assertThat(passwordHash).isNotEmpty()
    }

    @Test
    fun testGenSalt() {
        val salt = HashUtil.genSalt()
        assertThat(salt).isNotEmpty()
    }

    @Test
    fun testCheckHash() {
        val password = "password"
        val hash = HashUtil.getHash(password, salt)
        val result = HashUtil.checkHash(password, hash)
        assertThat(result).isTrue()
    }

    @Test
    fun testSha256() {
        val sha256Hash = HashUtil.sha256("text")
        assertThat(sha256Hash).hasSize(64)
    }
}
