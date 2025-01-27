package com.nexusorder.orderflow.util.storage

import com.fasterxml.jackson.module.kotlin.readValue
import com.nexusorder.orderflow.model.storage.Order
import com.nexusorder.orderflow.util.CoreObjectMapper
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

class OrderProductListConverter : AttributeConverter<List<Order.OrderProduct>> {

    override fun transformFrom(input: List<Order.OrderProduct>?): AttributeValue {
        return AttributeValue.builder()
            .s(CoreObjectMapper.writeValueAsString(input))
            .build()
    }

    override fun transformTo(input: AttributeValue?): List<Order.OrderProduct> {
        return input?.s()?.let { CoreObjectMapper.readValue(it) } ?: emptyList()
    }

    override fun type(): EnhancedType<List<Order.OrderProduct>> {
        return EnhancedType.listOf(Order.OrderProduct::class.java)
    }

    override fun attributeValueType(): AttributeValueType {
        return AttributeValueType.S
    }
}
