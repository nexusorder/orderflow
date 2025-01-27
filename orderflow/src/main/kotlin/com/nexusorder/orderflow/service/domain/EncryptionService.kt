package com.nexusorder.orderflow.service.domain

import com.nexusorder.orderflow.util.EncryptionUtil
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class EncryptionService(
    @Value("\${orderflow.encryption.key}") private val key: String,
    @Value("\${orderflow.encryption.iv}") private val iv: String
) {

    fun encrypt(data: String): String {
        return EncryptionUtil.encrypt(data, key, iv)
    }

    fun decrypt(data: String): String {
        return EncryptionUtil.decrypt(data, key, iv)
    }
}
