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
data class Category(
    @get:DynamoDbAttribute("key")
    @get:DynamoDbSecondaryPartitionKey(indexNames = ["key-index"])
    var key: String = "",
    @get:DynamoDbAttribute("name")
    var name: String = "",
    @get:DynamoDbAttribute("imageUrl")
    var imageUrl: String = "",
    @get:DynamoDbAttribute("enabled")
    var enabled: Boolean = true,
    @get:DynamoDbAttribute("order")
    var order: Long = 0,
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