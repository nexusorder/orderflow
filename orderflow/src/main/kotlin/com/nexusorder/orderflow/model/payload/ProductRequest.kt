package com.nexusorder.orderflow.model.payload

data class ProductRequest(
    val shopId: String = "",
    val name: String = "",
    val description: String = "",
    val price: Long = 0,
    val category: String = "",
    val imageUrl: String = ""
)
