package com.nexusorder.orderflow.util

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class EncryptionUtilTest {
    private val key = "WUohLgvmmCvj5kz9ph6RjA=="
    private val iv = "E0n3ldkmDVrL9EvREr/e6A=="

    @ParameterizedTest
    @CsvSource(
        "seoul,kQ9mzPeebSTs+uXOxQLABQ==",
        "busan,ufNacYqlmcfYRxPL0BozWg==",
        "daegu,KmQXDOxjtCw81r8g377FqA==",
        "incheon,NdvhB2gP/fhStUg2UsSv9g==",
        "gwangju,fM9NIJb7YLofjQxL3ZWEMQ==",
        "daejeon,xziv+FZrY8YWt15GraQdSg==",
        "ulsan,fK7xqvRE2EqOfsiBbNoRSA==",
        "sejong,hcpBeSbnVui9NmVYXuWf+A==",
        "gyeonggi,dzTy/XWAC9pBBIdcXlH/kg==",
        "gangwon,M1DUFHl/sd7v7zcXnQbHEA==",
        "chungbuk,luCDBn8Wrn3rBeXL7xRLmA==",
        "chungnam,gNjCXV78icLVhtlOwk21cA==",
        "jeonbuk,HORMeIv9H6bJ83fiz56/Jw=="
    )
    fun testEncrypt(decrypted: String, expected: String) {
        val actual = EncryptionUtil.encrypt(decrypted, key, iv)
        assertThat(actual).isEqualTo(expected)
    }

    @ParameterizedTest
    @CsvSource(
        "HORMeIv9H6bJ83fiz56/Jw==,jeonbuk",
        "gNjCXV78icLVhtlOwk21cA==,chungnam",
        "luCDBn8Wrn3rBeXL7xRLmA==,chungbuk",
        "M1DUFHl/sd7v7zcXnQbHEA==,gangwon",
        "dzTy/XWAC9pBBIdcXlH/kg==,gyeonggi",
        "hcpBeSbnVui9NmVYXuWf+A==,sejong",
        "fK7xqvRE2EqOfsiBbNoRSA==,ulsan",
        "xziv+FZrY8YWt15GraQdSg==,daejeon",
        "fM9NIJb7YLofjQxL3ZWEMQ==,gwangju",
        "NdvhB2gP/fhStUg2UsSv9g==,incheon",
        "KmQXDOxjtCw81r8g377FqA==,daegu",
        "ufNacYqlmcfYRxPL0BozWg==,busan",
        "kQ9mzPeebSTs+uXOxQLABQ==,seoul"
    )
    fun testDecrypt(encrypted: String, expected: String) {
        val actual = EncryptionUtil.decrypt(encrypted, key, iv)
        assertThat(actual).isEqualTo(expected)
    }
}
