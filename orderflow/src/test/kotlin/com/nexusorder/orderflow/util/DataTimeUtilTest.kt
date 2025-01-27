package com.nexusorder.orderflow.util

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.time.LocalTime

class DataTimeUtilTest {

    @Test
    fun testGetCurrentDatetime() {
        // when
        val actual = DataTimeUtil.getCurrentDatetime()
        val expectedPattern = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}"

        // then
        assertThat(actual).matches(expectedPattern)
    }

    @Test
    fun testIsOpened() {
        Mockito.mockStatic(LocalTime::class.java).use {
            // given
            val fixedTime = LocalTime.of(10, 0)
            it.`when`<LocalTime> { LocalTime.now() }.thenReturn(fixedTime)
            val openTime = "09:00:00"
            val closeTime = "18:00:00"

            // when
            val actual = DataTimeUtil.isOpened(openTime, closeTime)
            val expected = true

            // then
            assertThat(actual).isEqualTo(expected)
        }
    }
}
