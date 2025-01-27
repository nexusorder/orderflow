package com.nexusorder.orderflow.util

import com.nexusorder.orderflow.service.aws.CloudWatchLogService
import org.slf4j.LoggerFactory
import org.slf4j.event.Level

object CoreLogger {

    private val logger = LoggerFactory.getLogger(this::class.java)
    var cloudWatchLogService: CloudWatchLogService? = null

    fun info(
        mainKey: String,
        cKey: String = "",
        gcKey: String = "",
        message: Any,
        throwable: Throwable? = null,
        data: Any? = null,
        sendToCloudWatch: Boolean = true
    ) {
        log(Level.INFO, mainKey, cKey, gcKey, message, throwable, data, sendToCloudWatch)
    }

    fun warn(
        mainKey: String,
        cKey: String = "",
        gcKey: String = "",
        message: Any,
        throwable: Throwable? = null,
        data: Any? = null,
        sendToCloudWatch: Boolean = true
    ) {
        log(Level.WARN, mainKey, cKey, gcKey, message, throwable, data, sendToCloudWatch)
    }

    fun error(
        mainKey: String,
        cKey: String = "",
        gcKey: String = "",
        message: Any,
        throwable: Throwable? = null,
        data: Any? = null,
        sendToCloudWatch: Boolean = true
    ) {
        log(Level.ERROR, mainKey, cKey, gcKey, message, throwable, data, sendToCloudWatch)
    }

    fun debug(
        mainKey: String,
        cKey: String = "",
        gcKey: String = "",
        message: Any,
        throwable: Throwable? = null,
        data: Any? = null,
        sendToCloudWatch: Boolean = true
    ) {
        log(Level.DEBUG, mainKey, cKey, gcKey, message, throwable, data, sendToCloudWatch)
    }

    fun trace(
        mainKey: String,
        cKey: String = "",
        gcKey: String = "",
        message: Any,
        throwable: Throwable? = null,
        data: Any? = null,
        sendToCloudWatch: Boolean = true
    ) {
        log(Level.TRACE, mainKey, cKey, gcKey, message, throwable, data, sendToCloudWatch)
    }

    private fun generateKey(mainKey: String, cKey: String, gcKey: String): String {
        return "[$mainKey][$cKey][$gcKey]"
    }

    private fun log(
        level: Level,
        mainKey: String,
        cKey: String = "",
        gcKey: String = "",
        message: Any,
        throwable: Throwable? = null,
        data: Any? = null,
        sendToCloudWatch: Boolean = true
    ) {
        val key = generateKey(mainKey, cKey, gcKey)
        val logMessage = buildString {
            append("$key $message")
            data?.let { append(" | data: ${CoreObjectMapper.writeValueAsString(it)}") }
        }

        val loggerWithLevel = logger.atLevel(level)
        if (throwable != null) {
            loggerWithLevel.log(logMessage, throwable)
        } else {
            loggerWithLevel.log(logMessage)
        }

        if (sendToCloudWatch && cloudWatchLogService != null) {
            cloudWatchLogService!!.putLogEvents(
                mapOf(
                    "level" to level.toString(),
                    "message" to message,
                    "data" to data,
                    "throwable" to throwable.toString(),
                    "datetime" to DataTimeUtil.getCurrentDatetime()
                )
            )
        }
    }
}
