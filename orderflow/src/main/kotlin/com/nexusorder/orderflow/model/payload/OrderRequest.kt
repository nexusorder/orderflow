package com.nexusorder.orderflow.model.payload

data class OrderRequest(
    val shopId: String = "",
    val products: List<OrderProductRequest> = emptyList()
) {

    data class OrderProductRequest(
        val productId: String = "",
        val quantity: Long = 0,
    )
}
