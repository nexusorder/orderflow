package com.nexusorder.orderflow.model.payload

import com.fasterxml.jackson.annotation.JsonInclude
import com.nexusorder.orderflow.model.storage.Product

data class ProductResponse(
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val shopId: String = "",
    val name: String = "",
    val description: String = "",
    val price: Long = 0,
    val category: String = "",
    val imageUrl: String = "",
    val enabled: Boolean = true,
    val id: String
) {

    companion object {
        fun from(product: Product): ProductResponse {
            return ProductResponse(
                shopId = product.shopId,
                name = product.name,
                description = product.description,
                price = product.price,
                category = product.category,
                imageUrl = product.imageUrl,
                enabled = product.enabled,
                id = product.id
            )
        }
    }
}
