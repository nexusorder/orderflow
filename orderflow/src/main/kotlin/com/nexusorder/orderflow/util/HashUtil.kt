package com.nexusorder.orderflow.util

import org.mindrot.jbcrypt.BCrypt
import java.security.MessageDigest

object HashUtil {

    private val textSalt = "mWW9JrNjNXt3ldIPzPkDOKHW"
    private val salt = "\$2a\$10\$qct8bhb6VeZuQ.s4ZrJAee"
    private val digestSHA256 = MessageDigest.getInstance("SHA-256")

    fun getHash(text: String, salt: String = ""): String {
        return BCrypt.hashpw(text, salt)
    }

    fun getPasswordHash(text: String): String {
        val newText = text + textSalt
        return getHash(newText, salt)
    }

    fun genSalt(): String {
        return BCrypt.gensalt()
    }

    fun checkHash(one: String, another: String): Boolean {
        return BCrypt.checkpw(one, another)
    }

    fun sha256(text: String, salt: String = ""): String {
        val hashBytes = digestSHA256.digest((text + salt).toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}
