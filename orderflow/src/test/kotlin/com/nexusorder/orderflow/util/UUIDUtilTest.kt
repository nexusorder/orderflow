package com.nexusorder.orderflow.util

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UUIDUtilTest {

    @Test
    fun testGenerateUuid() {
        val uuid = UUIDUtil.generateUuid()
        assertThat(uuid).hasSize(32)
        assertThat(uuid).matches("[a-f0-9]{32}")
    }
}
