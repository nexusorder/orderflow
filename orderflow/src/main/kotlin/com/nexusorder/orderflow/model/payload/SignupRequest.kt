package com.nexusorder.orderflow.model.payload

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

data class SignupRequest(
    @Min(4) @Max(32)
    val login: String = "",
    @Min(8) @Max(256)
    val password: String = "",
    @Min(8) @Max(256)
    val passwordConfirm: String = "",
    @Min(2) @Max(32)
    val name: String = "",
    @Min(2) @Max(32)
    val nickname: String = "",
    @Min(6) @Max(14)
    val phone: String = "",
    @Min(7) @Max(128)
    val email: String = "",
    @Min(6) @Max(256)
    val address: String = "",
    @Min(1) @Max(64)
    val latitude: Double = 0.0,
    @Min(1) @Max(64)
    val longitude: Double = 0.0,
) {

    fun isValid(): Boolean {
        return password == passwordConfirm
    }
}
