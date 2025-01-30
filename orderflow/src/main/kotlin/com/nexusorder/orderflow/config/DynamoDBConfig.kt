package com.nexusorder.orderflow.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import java.net.URI

// DynamoDB 설정
@Configuration
@Profile("!test")
class DynamoDBConfig(
    @Value("\${aws.region:ap-northeast-2}")
    private val awsRegion: String,
    @Value("\${spring.profiles.active:default}")
    private val springProfilesActive: String
) {

    // https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/examples-dynamodb.html
    @Bean
    fun dynamoDbAsyncClient(): DynamoDbAsyncClient {
        return DynamoDbAsyncClient.builder()
            // default credentials located at: ~/.aws/credentials
            .credentialsProvider(DefaultCredentialsProvider.create())
            // default region located at: ~/.aws/config
            .region(Region.of(awsRegion))
            // 로컬 환경에서는 로컬 DDB을 사용하므로 로컬 주소로 endpoint를 설정
            .endpointOverride(if (springProfilesActive == "local") URI.create("http://localhost:8000") else null)
            .build()
    }

    // https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/dynamodb-enhanced-client.html
    @Bean
    fun dynamoDbEnhancedAsyncClient(dynamoDbAsyncClient: DynamoDbAsyncClient): DynamoDbEnhancedAsyncClient {
        return DynamoDbEnhancedAsyncClient.builder()
            .dynamoDbClient(dynamoDbAsyncClient)
            .build()
    }
}
