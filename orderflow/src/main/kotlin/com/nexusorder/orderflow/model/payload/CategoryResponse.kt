package com.nexusorder.orderflow.model.payload

import com.nexusorder.orderflow.model.storage.Category

data class CategoryResponse(
    val key: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val enabled: Boolean = true,
    val order: Long = 0,
) {

    companion object {
        fun from(category: Category): CategoryResponse {
            return CategoryResponse(
                key = category.key,
                name = category.name,
                imageUrl = category.imageUrl,
                enabled = category.enabled,
                order = category.order
            )
        }
    }
}
