package com.nexusorder.orderflow

import com.nexusorder.orderflow.repository.dynamodb.CategoryDynamoDBRepository
import com.nexusorder.orderflow.repository.dynamodb.MemberDynamoDBRepository
import com.nexusorder.orderflow.repository.dynamodb.OrderDynamoDBRepository
import com.nexusorder.orderflow.repository.dynamodb.ProductDynamoDBRepository
import com.nexusorder.orderflow.repository.dynamodb.ReviewDynamoDBRepository
import com.nexusorder.orderflow.repository.dynamodb.ShopDynamoDBRepository
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.mock.mockito.MockBeans

@SpringBootTest
@MockBeans(
    MockBean(CategoryDynamoDBRepository::class),
    MockBean(MemberDynamoDBRepository::class),
    MockBean(OrderDynamoDBRepository::class),
    MockBean(ProductDynamoDBRepository::class),
    MockBean(ReviewDynamoDBRepository::class),
    MockBean(ShopDynamoDBRepository::class)
)
class OrderflowTests {

    @Test
    fun contextLoads() {
    }
}
