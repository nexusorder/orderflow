package com.nexusorder.orderflow.service.domain

import com.nexusorder.orderflow.exception.MinimumOrderNotFulfilledException
import com.nexusorder.orderflow.model.payload.OrderRequest
import com.nexusorder.orderflow.model.payload.OrderResponse
import com.nexusorder.orderflow.model.storage.Order
import com.nexusorder.orderflow.service.storage.MemberStorageService
import com.nexusorder.orderflow.service.storage.OrderStorageService
import com.nexusorder.orderflow.service.storage.ProductStorageService
import com.nexusorder.orderflow.service.storage.ShopStorageService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class OrderService(
    private val memberStorageService: MemberStorageService,
    private val orderStorageService: OrderStorageService,
    private val shopStorageService: ShopStorageService,
    private val productStorageService: ProductStorageService,
) {

    // 주문 상세 조회
    fun findByIdAndMemberId(id: String, memberId: String): Mono<OrderResponse> {
        return orderStorageService.findById(id)
            .filter { it.memberId == memberId }
            .flatMap {
                shopStorageService.findById(it.shopId)
                    .flatMap { shop ->
                        productStorageService.findAll()
                            .filter { product -> it.products.firstOrNull { orderProduct -> product.id == orderProduct.productId } != null }
                            .collectList()
                            .map { products ->
                                OrderResponse.from(it, shop, products)
                            }
                    }
            }
    }

    // 주문 목록 조회
    fun findAllByMemberId(memberId: String): Mono<List<OrderResponse>> {
        return orderStorageService.findAllByMemberId(memberId)
            .collectList()
            .flatMap { orders ->
                // 주문에 포함된 가게를 찾습니다.
                shopStorageService.findAll()
                    .filter { shop -> orders.any { it.shopId == shop.id } }
                    .collectList()
                    .flatMap { shops ->
                        // 주문에 포함된 상품을 찾습니다.
                        productStorageService.findAll()
                            .filter { product -> orders.any { order -> order.products.firstOrNull { orderProduct -> product.id == orderProduct.productId } != null } }
                            .collectList()
                            .map { products ->
                                Pair(shops, products)
                            }
                    }.map { (shops, products) ->
                        orders.map { order ->
                            val shop = shops.first { it.id == order.shopId }
                            OrderResponse.from(order, shop, products)
                        }.sortedByDescending { it.createdAt }
                    }
            }
    }

    // 주문 생성
    fun save(memberId: String, request: OrderRequest): Mono<Order> {
        return Mono.zip(
            memberStorageService.findById(memberId),
            shopStorageService.findById(request.shopId)
        ).flatMap { tuple ->
            val member = tuple.t1
            val shop = tuple.t2
            // 주문에 포함된 상품을 찾습니다.
            productStorageService.findAll()
                .filter { product -> request.products.any { orderProduct -> product.id == orderProduct.productId } }
                .collectList()
                .map { products ->
                    Triple(shop, products, member)
                }
        }.flatMap inner@{ (shop, products, member) ->
            // 주문 모델을 생성합니다.
            val order = Order.from(request, shop, products, member)
                .copy(memberId = memberId)
            // 최소 주문 금액을 검증합니다.
            if (order.grandTotal < shop.minimumOrder) {
                throw MinimumOrderNotFulfilledException("Minimum order is ${shop.minimumOrder}; grand total is ${order.grandTotal}")
            }
            // 주문을 저장합니다.
            orderStorageService.save(order)
        }
    }
}
