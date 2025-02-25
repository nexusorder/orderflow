package com.nexusorder.orderflow.model.payload

import com.nexusorder.orderflow.model.storage.Product
import com.nexusorder.orderflow.model.storage.Shop
import com.nexusorder.orderflow.util.DataTimeUtil

data class ShopResponse(
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val phone: String = "",
    val email: String = "",
    val imageUrl: String = "",
    val isOpened: Boolean = true,
    val enabled: Boolean = true,
    val openTime: String = "",
    val closeTime: String = "",
    val category: String = "",
    val products: List<ProductResponse> = listOf(),
    val minimumOrder: Long = 0,
    val deliveryFee: Long = 0,
    val rating: Double = 5.0,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
) {

    companion object {

        fun from(shop: Shop, products: Collection<Product>): ShopResponse {
            return ShopResponse(
                id = shop.id,
                name = shop.name,
                address = shop.address,
                phone = shop.phone,
                imageUrl = shop.imageUrl,
                isOpened = DataTimeUtil.isOpened(shop.openTime, shop.closeTime),
                openTime = shop.openTime,
                closeTime = shop.closeTime,
                enabled = shop.enabled,
                category = shop.category,
                products = products.map { ProductResponse.from(it) },
                minimumOrder = shop.minimumOrder,
                deliveryFee = shop.deliveryFee,
                rating = shop.rating,
                latitude = shop.latitude,
                longitude = shop.longitude
            )
        }
    }
}
