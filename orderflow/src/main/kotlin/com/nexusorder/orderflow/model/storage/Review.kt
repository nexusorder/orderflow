package com.nexusorder.orderflow.model.storage

import com.nexusorder.orderflow.util.DataTimeUtil
import com.nexusorder.orderflow.util.UUIDUtil
import org.springframework.data.annotation.Version
import software.amazon.awssdk.enhanced.dynamodb.extensions.annotations.DynamoDbAutoGeneratedUuid
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey

@DynamoDbBean
data class Review(
    @get:DynamoDbAttribute("memberId")
    @get:DynamoDbSecondaryPartitionKey(indexNames = ["memberId-index"])
    var memberId: String = "",
    @get:DynamoDbAttribute("shopId")
    @get:DynamoDbSecondaryPartitionKey(indexNames = ["shopId-index"])
    var shopId: String = "",
    @get:DynamoDbAttribute("orderId")
    @get:DynamoDbSecondaryPartitionKey(indexNames = ["orderId-index"])
    var orderId: String = "",
    @get:DynamoDbAttribute("rating")
    var rating: Double = 0.0,
    @get:DynamoDbAttribute("comment")
    var comment: String = "",
    @get:DynamoDbAttribute("id")
    @get:DynamoDbPartitionKey
    @get:DynamoDbAutoGeneratedUuid
    override var id: String = UUIDUtil.generateUuid(),
    @get:DynamoDbAttribute("version")
    // @get:DynamoDbVersionAttribute
    @field:Version
    override var version: Long = 0L,
    @get:DynamoDbAttribute("createdAt")
    override var createdAt: String = DataTimeUtil.getCurrentDatetime(),
    @get:DynamoDbAttribute("updatedAt")
    override var updatedAt: String = DataTimeUtil.getCurrentDatetime()
) : AbstractCoreModel()