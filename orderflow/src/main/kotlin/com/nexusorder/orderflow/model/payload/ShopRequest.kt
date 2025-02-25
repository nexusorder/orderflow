package com.nexusorder.orderflow.model.payload

data class ShopRequest(
    val name: String = "",
    val address: String = "",
    val phone: String = "",
    val category: String = "",
    val imageUrl: String = "",
    val openTime: String = "",
    val closeTime: String = "",
    val minimumOrder: Long = 0,
    val deliveryFee: Long = 0,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
)
