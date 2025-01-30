package com.nexusorder.orderflow.util.storage

import com.fasterxml.jackson.module.kotlin.readValue
import com.nexusorder.orderflow.model.storage.Order
import com.nexusorder.orderflow.util.CoreObjectMapper
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

class OrderRefundConverter : AttributeConverter<Order.Refund> {

    override fun transformFrom(input: Order.Refund?): AttributeValue {
        return AttributeValue.builder()
            .s(CoreObjectMapper.writeValueAsString(input))
            .build()
    }

    override fun transformTo(input: AttributeValue?): Order.Refund {
        return input?.s()?.let { CoreObjectMapper.readValue(it) } ?: Order.Refund()
    }

    override fun type(): EnhancedType<Order.Refund> {
        return EnhancedType.of(Order.Refund::class.java)
    }

    override fun attributeValueType(): AttributeValueType {
        return AttributeValueType.S
    }
}
