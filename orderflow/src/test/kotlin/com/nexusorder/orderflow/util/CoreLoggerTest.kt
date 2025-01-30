package com.nexusorder.orderflow.util

import org.junit.jupiter.api.Test

class CoreLoggerTest {

    @Test
    fun testInfo() {
        CoreLogger.info("mainKey", message = "Info message")
    }

    @Test
    fun testWarn() {
        CoreLogger.warn("mainKey", message = "Warn message")
    }

    @Test
    fun testError() {
        CoreLogger.error("mainKey", message = "Error message")
    }

    @Test
    fun testDebug() {
        CoreLogger.debug("mainKey", message = "Debug message")
    }

    @Test
    fun testTrace() {
        CoreLogger.trace("mainKey", message = "Trace message")
    }
}
