package com.nexusorder.orderflow.model.storage

import com.nexusorder.orderflow.model.payload.ShopRequest
import com.nexusorder.orderflow.util.DataTimeUtil
import com.nexusorder.orderflow.util.UUIDUtil
import org.springframework.data.annotation.Version
import software.amazon.awssdk.enhanced.dynamodb.extensions.annotations.DynamoDbAutoGeneratedUuid
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey

@DynamoDbBean
data class Shop(
    @get:DynamoDbAttribute("name")
    var name: String = "",
    @get:DynamoDbAttribute("address")
    var address: String = "",
    @get:DynamoDbAttribute("phone")
    var phone: String = "",
    @get:DynamoDbAttribute("category")
    @get:DynamoDbSecondaryPartitionKey(indexNames = ["category-index"])
    var category: String = "",
    @get:DynamoDbAttribute("imageUrl")
    var imageUrl: String = "",
    @get:DynamoDbAttribute("enabled")
    var enabled: Boolean = true,
    @get:DynamoDbAttribute("visible")
    var visible: Boolean = true,
    @get:DynamoDbAttribute("openTime")
    var openTime: String = "",
    @get:DynamoDbAttribute("closeTime")
    var closeTime: String = "",
    @get:DynamoDbAttribute("rating")
    var rating: Double = 5.0,
    @get:DynamoDbAttribute("minimumOrder")
    var minimumOrder: Long = 0,
    @get:DynamoDbAttribute("deliveryFee")
    var deliveryFee: Long = 0,
    @get:DynamoDbAttribute("latitude")
    var latitude: Double = 0.0,
    @get:DynamoDbAttribute("longitude")
    var longitude: Double = 0.0,
    @get:DynamoDbAttribute("owner")
    @get:DynamoDbSecondaryPartitionKey(indexNames = ["owner-index"])
    var owner: String = "-1", // member id
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
) : AbstractCoreModel() {

    companion object {
        fun from(request: ShopRequest): Shop {
            return Shop(
                name = request.name,
                address = request.address,
                phone = request.phone,
                category = request.category,
                imageUrl = request.imageUrl,
                openTime = request.openTime,
                closeTime = request.closeTime,
                minimumOrder = request.minimumOrder,
                deliveryFee = request.deliveryFee,
                latitude = request.latitude,
                longitude = request.longitude
            )
        }
    }
}