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
data class Member(
    @get:DynamoDbAttribute("login")
    @get:DynamoDbSecondaryPartitionKey(indexNames = ["login-index"])
    var login: String = "", // login id
    @get:DynamoDbAttribute("password")
    var password: String = "", // hash
    @get:DynamoDbAttribute("name")
    var name: String = "",
    @get:DynamoDbAttribute("nickname")
    var nickname: String = "",
    @get:DynamoDbAttribute("email")
    var email: String = "",
    @get:DynamoDbAttribute("phone")
    var phone: String = "", // encrypted
    @get:DynamoDbAttribute("address")
    var address: String = "", // encrypted
    @get:DynamoDbAttribute("latitude")
    var latitude: String = "", // encrypted
    @get:DynamoDbAttribute("longitude")
    var longitude: String = "", // encrypted
    @get:DynamoDbAttribute("seller")
    var seller: Boolean = false,
    @get:DynamoDbAttribute("shopId")
    var shopId: String = "", // if only seller
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
