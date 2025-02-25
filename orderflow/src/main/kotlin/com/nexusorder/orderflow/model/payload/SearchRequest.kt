package com.nexusorder.orderflow.model.payload

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

data class SearchRequest(
    @field:Min(2) @field:Max(256)
    val name: String = "",
    @field:Min(2) @field:Max(256)
    val category: String = "",
)
