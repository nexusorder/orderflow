package com.nexusorder.orderflow.util

import java.util.UUID

object UUIDUtil {

    fun generateUuid(): String {
        return UUID.randomUUID().toString().replace("-", "")
    }
}
