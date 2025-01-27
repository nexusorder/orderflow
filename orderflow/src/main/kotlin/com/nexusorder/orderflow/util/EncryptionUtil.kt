package com.nexusorder.orderflow.util

import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object EncryptionUtil {
    private val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    private const val ALGORITHM = "AES"
    private val base64Encoder = Base64.getEncoder()
    private val base64Decoder = Base64.getDecoder()

    fun encrypt(value: String, key: String, iv: String): String {
        val keySpec = SecretKeySpec(base64Decoder.decode(key), ALGORITHM)
        val ivSpec = IvParameterSpec(base64Decoder.decode(iv))
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
        val encrypted = cipher.doFinal(value.toByteArray())
        return base64Encoder.encodeToString(encrypted)
    }

    fun decrypt(value: String, key: String, iv: String): String {
        val keySpec = SecretKeySpec(base64Decoder.decode(key), ALGORITHM)
        val ivSpec = IvParameterSpec(base64Decoder.decode(iv))
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
        val decodedValue = base64Decoder.decode(value)
        val decrypted = cipher.doFinal(decodedValue)
        return String(decrypted)
    }

    fun createKey(): String {
        val key = (0..15)
            .map { (Math.random() * 256).toInt().toByte() }
            .toByteArray()
        return base64Encoder.encodeToString(key)
    }

    fun createIV(): String {
        val iv = (0..15)
            .map { (Math.random() * 256).toInt().toByte() }
            .toByteArray()
        return base64Encoder.encodeToString(iv)
    }
}
