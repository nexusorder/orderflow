# Level 3: 백엔드 로직 구현
## Task 7
- 소제목: 인증 및 권한 관리(Session)
- 멘토 코멘트: 사용자에게 허용된 권한을 관리하는 방법을 살펴봅니다.
- 소요시간: 30분
- 답변 Type : 장문형

API는 사용자에게 제공하는 용도와 시스템 관리자 용도로 나눌 수 있는데요.
외부 노출을 위한 API path는 /api/**로 지정하고자 합니다.
Spring Security 의존성을 사용해서 권한 제어하는 기능을 작성해주세요.
그리고 Session을 이용해 사용자의 로그인 정보를 담아서 접근 가능한 정보(주문 목록 등)에만 접근하도록 개발해주세요.

- 정답:
1. path별 권한 제어
> spring security 의존성을 활용한 권한 제어 코드의 경우 정답
```kt
@Configuration
// org.springframework.boot:spring-boot-starter-security 의존성을 사용합니다.
@EnableWebFluxSecurity
class SecurityConfig {

    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .authorizeExchange { exchanges ->
                exchanges
                    // 특정 path에 대해 허용할 수 있습니다.
                    .pathMatchers("/api/**").permitAll()
                    .pathMatchers("/health").permitAll()
                    // 나머지 path에 대해 인증을 요구할 수 있습니다.
                    .anyExchange().authenticated()
            }
            .httpBasic(withDefaults())
            .formLogin(withDefaults())
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .build()
    }
}
```
2. Session 이용 접근 제한
> ServerWebExchange 객체의 attributes를 사용해 로그인 유저를 구분하는 코드의 경우 모두 정답

```kt
@GetMapping
fun findAllByMemberId(exchange: ServerWebExchange): Mono<ResponseEntity<ApiResponse<List<OrderResponse>>>> {
    return exchange.session.mapNotNull { session ->
        // ServerWebExchange 객체의 attributes에 로그인 정보를 기록해 세션 정보를 활용합니다.
        // 아래의 경우 LOGIN_KEY = "login"에 memberId를 저장해서 서버단에서 관리합니다.
        // 이 방식은 서버가 재시작되는 경우 세션 정보가 삭제돼 의도치 않은 로그아웃을 발생시킬 수 있습니다.
        // 서버가 재시작되어도 로그인이 유지되는 방법을 찾아보세요.
        session.attributes[LOGIN_KEY] as String?
    }.flatMap { memberId ->
        orderService.findAllByMemberId(memberId!!)
            .map {
                ResponseEntity.ok(
                    ApiResponse.success(it)
                )
            }
    }.unauthorizedIfEmpty()
}
```


## Task 8
- 소제목: 로컬 데이터베이스 연동(DynamoDB)
- 멘토 코멘트: DynamoDB 로컬 인스턴스를 생성하고 연결해봅시다.
- 소요시간: 60분
- 답변 Type : 장문형

DynamoDB를 쉽게 사용하기 위한 GUI인 [NoSQL Workbench](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/workbench.settingup.html)라는 도구가 있습니다.
해당 파일을 플랫폼에 맞게 다운로드, 설치합니다.
설치 후에 실행하고 DDB Local이라는 옵션을 활성화해 8000포트에 DynamoDB 인스턴스를 생성합니다.
Operation Builder 메뉴에 들어가 category, member, shop, product, order 테이블을 각각 id를 파티션 키로 지정하여 생성합니다.
GSI는 Task 6의 ERD에서 FK로 지정된 키를 파티션 키로, 이름은 `${key}-index`로 지정해서 생성해주세요.
이렇게되면 DB 연결을 기본적인 준비는 끝났습니다.

다음으로 Local DynamoDB와 연결하기 위한 Configuration과 Repository 코드를 작성해주세요.
비동기 작업을 위해 DynamoDbEnhancedAsyncClient의 Bean을 생성하고 이용해야합니다.

- 정답:
> DynamoDB를 연결하기 위한 Configuration 코드를 작성하고
> 조회, 생성, 삭제 메서드를 가지는 Repository 코드를 작성했다면 정답

```kt
// DynamoDB 설정
@Configuration
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
```

```kt
// 재사용을 위한 추상 클래스 DynamoDB 레포지토리
abstract class AbstractDynamoDBRepository<T : AbstractCoreModel>(
    dynamoDbEnhancedAsyncClient: DynamoDbEnhancedAsyncClient,
    private val tableName: String,
    clazz: Class<T>
) : ReactiveCrudRepository<T, String> {

    // 주어진 테이블 이름으로 객체를 생성합니다.
    protected val table: DynamoDbAsyncTable<T> =
        dynamoDbEnhancedAsyncClient.table(tableName, TableSchema.fromBean(clazz))

    // 조회
    override fun findById(id: String): Mono<T> {
        return Mono.fromFuture(
            table.getItem { builder ->
                // 기본 키(파티션 키)를 통해 조회합니다.
                builder.key { it.partitionValue(id) }
            }
        )
    }

    // 저장
    override fun <S : T> save(entity: S): Mono<S> {
        return Mono.fromFuture(table.putItem(entity))
            .thenReturn(entity)
    }

    // 삭제
    override fun delete(entity: T): Mono<Void> {
        return Mono.fromFuture(
            table.deleteItem {
                it.key {
                    // 기본 키(파티션 키)를 통해 삭제합니다.
                    it.partitionValue(entity.id)
                }
            }
        ).then()
    }
}
```

이를 활용하기 위해서는 아래와 같이 DynamoDb 어노테이션을 사용한 객체 정의가 필요합니다.
`@DynamoDbAttribute`는 해당 객체 필드와 연결된 DynamoDB 내 필드 이름을 정의합니다.
`@DynamoDbSecondaryPartitionKey`은 GSI를 위한 인덱스 이름을 정의합니다.

```kt
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
    @field:Version
    override var version: Long = 0L,
    @get:DynamoDbAttribute("createdAt")
    override var createdAt: String = DataTimeUtil.getCurrentDatetime(),
    @get:DynamoDbAttribute("updatedAt")
    override var updatedAt: String = DataTimeUtil.getCurrentDatetime()
) : AbstractCoreModel()
```

## Task 9
- 소제목: 비즈니스 로직 구현(가게 조회, 주문 등)
- 멘토 코멘트: 서비스에 필요한 핵심 로직를 구현합니다.
- 소요시간: 960분
- 답변 Type : 장문형

데이터베이스 연결을 마치셨으니 이제 핵심 로직을 개발할 차례입니다.
Task 1에서 서비스 핵심 기능을 크게 4가지로 정의했었는데요.
회원가입/로그인, 가게 검색, 가게 상세 조회, 주문 서비스 로직을 개발하고 DB를 통해 저장, 조회할 수 있도록 작업해주세요.

회원가입/로그인에서는 암호와 개인정보를 어떻게 처리할 것인가가 중요한 문제입니다.
개인 정보를 암호화하여 데이터베이스가 유출되더라도 피해를 최소화하는 과정은 데이터 보호에 있어서 필수적인 과정입니다.
특히 암호의 경우 AES256과 같은 양방향 암호화가 아닌 SHA256 같은 해시 함수를 이용하는데요.
왜 해시 함수를 사용하는 게 바람직한지 고민해보셨으면 좋겠어요.

가게 검색에서는 가게명과 메뉴명으로 검색이 지원되어야합니다.
가게명과 메뉴명으로 각각 검색할 경우 중복되는 가게가 있을 수 있기에
중복 처리를 신경써서 작업을 진행해주세요.

가게 상세 조회에서는 Shop과 Product 테이블이 분리되어 있기 때문에
두 테이블을 조인해서 뷰를 제공하는 로직이 필요합니다.

주문 서비스에서는 사용자가 주문 목록을 사용자 아이디로 조회할 수 있도록해야하고,
주문 요청 시 상품의 가격이 아닌 상품 아이디만으로 주문을 생성하도록 해야합니다.
클라이언트에서 가격 정보나 상품의 데이터를 제공받게되면
파라미터 변조 취약점에 노출되어 서비스에 피해를 유발할 수 있게됩니다.
또한, 주문 시 최소 주문 금액의 유효성을 판단하고 무효할 경우
오류를 반환하는 로직도 필요합니다.


- 정답:
1. 회원가입/로그인
```kt
@Service
class AuthService(val memberStorageService: MemberStorageService) {
    // 로그인
    fun login(request: LoginRequest): Mono<Member> {
        return memberStorageService.findByLogin(request.login)
            .mapNotNull {
                // 암호는 보안을 위해 해시를 통해 검증해야합니다.
                if (it.password == HashUtil.getPasswordHash(request.password)) {
                    it
                } else {
                    null
                }
            }
    }

    fun signup(request: SignupRequest): Mono<Member> {
        return memberStorageService.existsByLogin(request.login.trim())
            .flatMap {
                if (it) {
                    Mono.empty()
                } else {
                    // 암호는 보안을 위해 해시를 통해 저장해야합니다.
                    // 양방향 암호화가 아닌 해시를 사용하는 이유를 생각해보세요.
                    memberStorageService.save(
                        Member(
                            login = request.login.trim(),
                            password = HashUtil.getPasswordHash(request.login.trim() + request.password),
                            name = request.name.trim(),
                            nickname = request.nickname.trim(),
                            email = request.email.trim(),
                            phone = request.phone.trim(),
                            address = request.address.trim(),
                            latitude = request.latitude.toString(),
                            longitude = request.longitude.toString(),
                        )
                    )
                }
            }
    }

    // 사용자 정보 조회
    // 세션 정보를 통해 로그인 사용자와 일치할 때만 조회가 가능해야합니다.
    fun profile(id: String): Mono<ProfileResponse> {
        return memberStorageService.findById(id)
            .map { ProfileResponse.of(it) }
    }

    companion object {
        const val LOGIN_KEY = "login"
    }
}
```

2. 가게 검색
```kt
@Service
class SearchService(
    private val shopStorageService: ShopStorageService,
    private val productStorageService: ProductStorageService
) {

    // 검색
    fun search(request: SearchRequest): Mono<List<ShopResponse>> {
        // 검색 대상이 없을 경우 빈 리스트를 반환합니다.
        if (StringUtils.isBlank(request.name) && StringUtils.isBlank(request.category)) {
            return Mono.just(emptyList())
        }

        return Mono.zip(
            // 가게 중 검색어에 해당하는 가게를 찾습니다.
            shopStorageService.findAll()
                .filter {
                    (StringUtils.isBlank(request.name) || it.name.contains(request.name, ignoreCase = true)) &&
                            (StringUtils.isBlank(request.category) || it.category.contains(
                                request.category,
                                ignoreCase = true
                            ))
                }
                .collectList(),
            // 상품 중 검색어에 해당하는 상품을 찾습니다.
            productStorageService.findAll()
                .filter {
                    (StringUtils.isBlank(request.name) || it.name.contains(request.name, ignoreCase = true)) &&
                            (StringUtils.isBlank(request.category) || it.category.contains(
                                request.category,
                                ignoreCase = true
                            ))
                }
                .map { it.shopId }
                .distinct()
                // 상품에 해당하는 가게를 찾습니다.
                .flatMap { shopStorageService.findById(it) }
                .collectList()
        ).flatMapMany { tuple ->
            // 가게와 상품을 합쳐서 중복을 제거합니다.
            Flux.fromIterable((tuple.t1 + tuple.t2).distinctBy { it.id })
        }.flatMap {
            // 가게에 해당하는 상품을 찾습니다.
            productStorageService.findAllByShopId(it.id)
                .collectList()
                // 가게와 상품을 합쳐서 반환합니다.
                .map { products -> ShopResponse.from(it, products) }
        }.collectList()
    }
}
```

3. 가게 상세 조회
```kt
@Service
class ShopService(
    private val shopStorageService: ShopStorageService,
    private val productStorageService: ProductStorageService
) {

    // 가게 상세 조회
    fun findById(id: String): Mono<ShopResponse> {
        return shopStorageService.findById(id)
            .flatMap {
                // 가게에 해당하는 상품을 찾습니다.
                productStorageService.findAllByShopId(it.id)
                    .collectList()
                    .map { products ->
                        // 가게와 상품을 합쳐서 반환합니다.
                        ShopResponse.from(it, products)
                    }
            }
    }

    // 가게 목록 조회
    fun findAll(): Mono<List<ShopResponse>> {
        return shopStorageService.findAll()
            .flatMap {
                // 가게에 해당하는 상품을 찾습니다.
                productStorageService.findAllByShopId(it.id)
                    .collectList()
                    .map { products ->
                        // 가게와 상품을 합쳐서 반환합니다.
                        ShopResponse.from(it, products)
                    }
            }.collectList()
    }

    // 가게 생성
    fun save(@RequestBody @Valid request: ShopRequest): Mono<ShopResponse> {
        return shopStorageService.save(
            // 요청을 가게 모델로 변환합니다.
            Shop.from(request)
        ).map {
            ShopResponse.from(it, listOf())
        }
    }

    // 추천 가게 조회
    fun recommend(@RequestParam @Valid @Min(1) count: Int = 1): Mono<List<ShopResponse>> {
        return shopStorageService.findAll()
            .collectList().map { shops ->
                // 가게 목록을 무작위로 배열한 후 count 만큼 반환합니다.
                shops.shuffled().take(count)
            }.flatMapMany {
                Flux.fromIterable(it)
            }.flatMap {
                // 가게에 해당하는 상품을 찾습니다.
                productStorageService.findAllByShopId(it.id).collectList()
                    .map { products ->
                        // 가게와 상품을 합쳐서 반환합니다.
                        ShopResponse.from(it, products)
                    }
            }.collectList()
    }
}
```

4. 주문
```kt
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
```