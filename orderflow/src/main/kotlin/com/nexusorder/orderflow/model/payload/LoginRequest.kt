package com.nexusorder.orderflow.model.payload

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

data class LoginRequest(
    @Min(4) @Max(32)
    val login: String = "",
    @Min(8) @Max(256)
    val password: String = "",
)
