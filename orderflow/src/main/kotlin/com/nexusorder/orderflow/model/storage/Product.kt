package com.nexusorder.orderflow.model.storage

import com.nexusorder.orderflow.model.payload.ProductRequest
import com.nexusorder.orderflow.util.DataTimeUtil
import com.nexusorder.orderflow.util.UUIDUtil
import org.springframework.data.annotation.Version
import software.amazon.awssdk.enhanced.dynamodb.extensions.annotations.DynamoDbAutoGeneratedUuid
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey

@DynamoDbBean
data class Product(
    @get:DynamoDbAttribute("shopId")
    @get:DynamoDbSecondaryPartitionKey(indexNames = ["shopId-index"])
    var shopId: String = "",
    @get:DynamoDbAttribute("name")
    var name: String = "",
    @get:DynamoDbAttribute("description")
    var description: String = "",
    @get:DynamoDbAttribute("price")
    var price: Long = 0,
    @get:DynamoDbAttribute("category")
    @get:DynamoDbSecondaryPartitionKey(indexNames = ["category-index"])
    var category: String = "",
    @get:DynamoDbAttribute("imageUrl")
    var imageUrl: String = "",
    @get:DynamoDbAttribute("enabled")
    var enabled: Boolean = true,
    @get:DynamoDbAttribute("visible")
    var visible: Boolean = true,
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
        fun from(request: ProductRequest): Product {
            return Product(
                shopId = request.shopId,
                name = request.name,
                description = request.description,
                price = request.price,
                category = request.category,
                imageUrl = request.imageUrl
            )
        }
    }
}
