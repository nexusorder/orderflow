package com.nexusorder.orderflow.util

import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object DataTimeUtil {
    private val DATETIME_FORMATTER_FULL = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    private val DATETIME_FORMATTER_TIME = DateTimeFormatter.ofPattern("HH:mm:ss")

    fun getCurrentDatetime(): String {
        return LocalDateTime.now().format(DATETIME_FORMATTER_FULL)
    }

    fun isOpened(openTime: String, closeTime: String): Boolean {
        val now = LocalTime.now()
        try {
            val open = LocalTime.parse(openTime, DATETIME_FORMATTER_TIME)
            val close = LocalTime.parse(closeTime, DATETIME_FORMATTER_TIME)
            return !now.isBefore(open) && !now.isAfter(close)
        } catch (e: Exception) {
            CoreLogger.error("DataTimeUtil", message = "Failed to parse time: $openTime, $closeTime")
            return true
        }
    }
}
