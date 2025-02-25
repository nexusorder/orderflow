package com.nexusorder.orderflow.model.payload

import com.fasterxml.jackson.annotation.JsonInclude
import com.nexusorder.orderflow.constant.DeliveryStatus
import com.nexusorder.orderflow.constant.OrderStatus
import com.nexusorder.orderflow.constant.PaymentMethod
import com.nexusorder.orderflow.constant.PaymentStatus
import com.nexusorder.orderflow.constant.RefundStatus
import com.nexusorder.orderflow.model.storage.Order
import com.nexusorder.orderflow.model.storage.Product
import com.nexusorder.orderflow.model.storage.Shop
import com.nexusorder.orderflow.util.DataTimeUtil
import com.nexusorder.orderflow.util.UUIDUtil

data class OrderResponse(
    val memberId: String = "",
    val shop: ShopResponse = ShopResponse(),
    val products: List<OrderProductResponse> = emptyList(),
    val status: OrderStatus = OrderStatus.PENDING,
    val reviewId: String = "",
    val deliveryAddress: String = "",
    val deliveryPhone: String = "",
    val deliveredTime: String = "",
    val deliveryFee: Long = 0,
    val grandTotal: Long = 0,
    val paymentMethod: PaymentMethod = PaymentMethod.EMPTY,
    val paymentStatus: PaymentStatus = PaymentStatus.PENDING,
    val deliveryStatus: DeliveryStatus = DeliveryStatus.PENDING,
    val refund: OrderRefundResponse = OrderRefundResponse(),
    val id: String = UUIDUtil.generateUuid(),
    val createdAt: String = DataTimeUtil.getCurrentDatetime(),
) {

    data class OrderProductResponse(
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        val shopId: String = "",
        val name: String = "",
        val description: String = "",
        val price: Long = 0,
        val category: String = "",
        val imageUrl: String = "",
        val enabled: Boolean = true,
        val id: String,
        val quantity: Long = 0,
    ) {
        companion object {
            fun from(product: Product, quantity: Long): OrderProductResponse {
                return OrderProductResponse(
                    shopId = product.shopId,
                    name = product.name,
                    description = product.description,
                    price = product.price,
                    category = product.category,
                    imageUrl = product.imageUrl,
                    enabled = product.enabled,
                    id = product.id,
                    quantity = quantity
                )
            }
        }
    }

    data class OrderRefundResponse(
        val refundStatus: RefundStatus = RefundStatus.PENDING,
        val refundReason: String = "",
        val refundRequestedDatetime: String = "",
        val refundCompletedDatetime: String = "",
        val refundPaymentMethod: PaymentMethod = PaymentMethod.EMPTY,
        val refundAmount: Int = 0,
    ) {
        companion object {
            fun from(refund: Order.Refund): OrderRefundResponse {
                return OrderRefundResponse(
                    refundStatus = refund.refundStatus,
                    refundReason = refund.refundReason,
                    refundRequestedDatetime = refund.refundRequestedDatetime,
                    refundCompletedDatetime = refund.refundCompletedDatetime,
                    refundPaymentMethod = refund.refundPaymentMethod,
                    refundAmount = refund.refundAmount
                )
            }
        }
    }

    companion object {
        fun from(order: Order, shop: Shop, products: List<Product>): OrderResponse {
            return OrderResponse(
                memberId = order.memberId,
                shop = ShopResponse.from(shop, products),
                products = order.products.mapNotNull { orderProduct ->
                    val product = products.firstOrNull { it.id == orderProduct.productId }
                    product?.let { OrderProductResponse.from(it, orderProduct.quantity) }
                },
                status = order.status,
                reviewId = order.reviewId,
                deliveryAddress = order.deliveryAddress,
                deliveryPhone = order.deliveryPhone,
                deliveredTime = order.deliveredTime,
                deliveryFee = order.deliveryFee,
                grandTotal = order.grandTotal,
                paymentMethod = order.paymentMethod,
                paymentStatus = order.paymentStatus,
                deliveryStatus = order.deliveryStatus,
                refund = OrderRefundResponse.from(order.refund),
                id = order.id,
                createdAt = order.createdAt
            )
        }
    }
}
