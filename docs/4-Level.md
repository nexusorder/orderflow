# Level 4: 테스트와 배포
## Task 10
- 소제목: 단위 및 통합 테스트
- 멘토 코멘트: 테스트 코드를 작성해봅니다.
- 소요시간: 30분
- 답변 Type : 장문형

비즈니스 로직을 개발하시느라 고생하셨습니다.
가장 큰 산을 넘으셨어요. 👏 
코드가 정상 동작하는지 확인하려면 테스트 수행이 중요한데요,
각 서비스별 기능이 정상 동작하는지 확인하기 위한 단위 테스트 코드를 작성해주세요.
그리고 컨트롤러의 요청/응답 확인을 통한 통합 테스트 코드를 작성해주세요.
Webflux를 Reactor를 사용하는데요,
[projectreactor testing](https://projectreactor.io/docs/core/release/reference/testing.html) 문서를 확인하면 도움이 되실겁니다.

- 정답:
> 서비스 컴포넌트에 대한 유닛 테스트 코드를 작성하고 모두 통과하면 정답
> 컨트롤러에 대한 통합 테스트 코드를 작성하고 모두 통과하면 정답

```kt
// 인증 서비스 테스트
class AuthServiceTest {

    // CUT(Class Under Test)
    private lateinit var authService: AuthService

    // Dependencies
    private lateinit var memberStorageService: MemberStorageService

    // Mock repository
    private val repository = mutableMapOf<String, Member>()

    @BeforeEach
    fun setUp() {
        memberStorageService = createMockMemberStorageService()
        authService = AuthService(memberStorageService)
    }

    private fun createMockMemberStorageService(): MemberStorageService {
        repository.clear()
        repository["memberId"] = Member(id = "id1", login = "memberId", password = "memberPasswordHash")

        return mock<MemberStorageService> {
            on { findByLogin(any()) }.thenAnswer { invocation ->
                val login = invocation.getArgument<String>(0)
                Mono.justOrEmpty(repository.values.find { it.login == login })
            }
            on { findById(any()) }.thenAnswer { invocation ->
                val id = invocation.getArgument<String>(0)
                Mono.justOrEmpty(repository[id])
            }
            on { existsByLogin(any()) }.thenAnswer { invocation ->
                val login = invocation.getArgument<String>(0)
                Mono.just(repository.values.any { it.login == login })
            }
        }
    }

    @Nested
    @DisplayName("로그인")
    inner class LoginTest {

        @Test
        @DisplayName("사용자가 존재하고 비밀번호 해시가 일치하면 로그인 성공한다")
        fun testLoginSuccess() {
            // given
            val request = LoginRequest(
                login = "memberId",
                password = "memberPasswordHash"
            )

            // when
            authService.login(request)
                .test()
                .expectNextMatches {
                    it.login == "memberId" &&
                            it.password == "memberPasswordHash"
                }
                .expectComplete()
        }

        @Test
        @DisplayName("사용자가 존재하고 비밀번호 해시가 일치하지 않으면 로그인 실패한다")
        fun testLoginFailWithPassword() {
            // given
            val request = LoginRequest(
                login = "memberId",
                password = "memberPasswordHashWrong"
            )

            // when
            authService.login(request)
                .test()
                .expectComplete()
        }

        @Test
        @DisplayName("사용자가 존재하지 않으면 로그인 시 빈 값을 반환한다")
        fun testLoginFailWithLogin() {
            // given
            val request = LoginRequest(
                login = "memberId2",
                password = "memberPassword2"
            )

            // when
            authService.login(request)
                .test()
                .expectComplete()
        }
    }

    @Nested
    @DisplayName("회원가입")
    inner class SignupTest {

        @Test
        @DisplayName("사용자가 존재하지 않으면 회원가입 성공한다")
        fun testSignupSuccess() {
            // given
            val request = SignupRequest(
                login = "memberId2",
                password = "memberPasswordHash2"
            )

            // when
            authService.signup(request)
                .test()
                .expectNextMatches {
                    it.login == "memberId2" &&
                            it.password == "memberPasswordHash2"
                }
                .expectComplete()
        }

        @Test
        @DisplayName("사용자가 존재하면 회원가입 시 빈 값을 반환한다")
        fun testSignupFail() {
            // given
            val request = SignupRequest(
                login = "memberId",
                password = "memberPasswordHash"
            )

            // when
            authService.signup(request)
                .test()
                .expectComplete()
        }
    }

    @Nested
    @DisplayName("프로파일")
    inner class ProfileTest {

        @Test
        @DisplayName("존재하는 사용자 프로필 조회 성공한다")
        fun testProfileSuccess() {
            // when
            authService.profile("id1")
                .test()
                .expectNextMatches {
                    it.login == "memberId"
                }
                .expectComplete()
        }

        @Test
        @DisplayName("존재하지 않는 사용자 프로필 조회 시 빈 값을 반환한다")
        fun testProfileFail() {
            // when
            authService.profile("id2")
                .test()
                .expectComplete()
        }
    }
}
```

```kt
// 인증 통합 테스트
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class AuthIntegrationTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    // Dependency Injection
    @MockBean
    private lateinit var memberStorageService: MemberStorageService

    @Autowired
    private lateinit var authService: AuthService

    // Mock repository
    private val repository = mutableMapOf<String, Member>()

    @BeforeEach
    fun setUp() {
        mockMemberStorageService()
    }

    private fun mockMemberStorageService() {
        repository.clear()
        repository["id1"] = Member(
            id = "id1",
            login = "memberId",
            password = HashUtil.getPasswordHash("memberPasswordHash")
        )

        given {
            memberStorageService.findByLogin(any())
        } willAnswer { invocation ->
            val login = invocation.getArgument<String>(0)
            Mono.justOrEmpty(repository.values.find { it.login == login })
        }
        given {
            memberStorageService.findById(any())
        } willAnswer { invocation ->
            CoreLogger.info("!".repeat(50), message = "invocation: $invocation")
            val id = invocation.getArgument<String>(0)
            Mono.justOrEmpty(repository[id])
        }
        given {
            memberStorageService.existsByLogin(any())
        } willAnswer { invocation ->
            val login = invocation.getArgument<String>(0)
            Mono.just(repository.values.any { it.login == login })
        }
        given {
            memberStorageService.save(any())
        } willAnswer { invocation ->
            val member = invocation.getArgument<Member>(0)
            repository[member.id] = member
            Mono.just(member)
        }
    }

    @Nested
    @DisplayName("로그인")
    inner class LoginTest {

        @Test
        @DisplayName("사용자가 존재하고 비밀번호 해시가 일치하면 로그인 성공한다")
        fun testLoginSuccess() {
            // given
            val request = LoginRequest(
                login = "memberId",
                password = "memberPasswordHash"
            )

            // when
            webTestClient.post()
                .uri("/api/v1/auth/login")
                .body(Mono.just(request), LoginRequest::class.java).exchange()
                // then
                .expectStatus().isOk
                .expectBody(ApiResponse::class.java)
                .consumeWith { response ->
                    val body = response.responseBody
                    assertThat(body).isNotNull
                    assertThat(body?.success).isTrue()
                    assertThat(body?.data).isEqualTo(true)
                }
        }

        @Test
        @DisplayName("사용자가 존재하고 비밀번호 해시가 일치하지 않으면 로그인 실패한다")
        fun testLoginFailWithPassword() {
            // given
            val request = LoginRequest(
                login = "memberId",
                password = "memberPasswordHashWrong"
            )

            // when
            webTestClient.post()
                .uri("/api/v1/auth/login")
                .body(Mono.just(request), LoginRequest::class.java).exchange()
                // then
                .expectStatus().isOk
                .expectBody(ApiResponse::class.java)
                .consumeWith { response ->
                    val body = response.responseBody
                    assertThat(body).isNotNull
                    assertThat(body?.success).isTrue()
                    assertThat(body?.data).isEqualTo(false)
                }
        }

        @Test
        @DisplayName("사용자가 존재하지 않으면 로그인 실패한다")
        fun testLoginFailWithLogin() {
            // given
            val request = LoginRequest(
                login = "memberId2",
                password = "memberPassword2"
            )

            // when
            webTestClient.post().uri("/api/v1/auth/login")
                .body(Mono.just(request), LoginRequest::class.java)
                .exchange()
                // then
                .expectStatus().isOk
                .expectBody(ApiResponse::class.java)
                .consumeWith { response ->
                    val body = response.responseBody
                    assertThat(body).isNotNull
                    assertThat(body?.success).isTrue()
                    assertThat(body?.data).isEqualTo(false)
                }
        }
    }

    @Nested
    @DisplayName("회원가입")
    inner class SignupTest {

        @Test
        @DisplayName("사용자가 존재하지 않으면 회원가입 성공한다")
        fun testSignupSuccess() {
            // given
            val request = SignupRequest(
                login = "memberId2",
                password = "memberPasswordHash2",
                passwordConfirm = "memberPasswordHash2"
            )

            // when
            webTestClient.post().uri("/api/v1/auth/signup")
                .body(Mono.just(request), SignupRequest::class.java)
                .exchange()
                // then
                .expectStatus().isOk
                .expectBody(ApiResponse::class.java)
                .consumeWith { response ->
                    val body = response.responseBody
                    assertThat(body).isNotNull
                    assertThat(body?.success).isTrue()
                    assertThat(body?.data).isEqualTo(true)
                }
        }

        @Test
        @DisplayName("사용자가 존재하면 회원가입 실패한다")
        fun testSignupFail() {
            // given
            val request = SignupRequest(
                login = "memberId",
                password = "memberPasswordHash",
                passwordConfirm = "memberPasswordHash"
            )

            // when
            webTestClient.post().uri("/api/v1/auth/signup").body(Mono.just(request), SignupRequest::class.java)
                .exchange()
                // then
                .expectStatus().isOk
                .expectBody(ApiResponse::class.java)
                .consumeWith { response ->
                    val body = response.responseBody
                    assertThat(body).isNotNull
                    assertThat(body?.success).isTrue()
                    assertThat(body?.data).isEqualTo(false)
                }
        }
    }

    @Nested
    @DisplayName("프로파일")
    inner class ProfileTest {

        @Test
        @DisplayName("로그인된 사용자 프로필 조회 성공한다")
        fun testProfileSuccess() {
            // when
            webTestClient.mutateWith(
                SessionMutator(mapOf(AuthService.LOGIN_KEY to "id1"))
            )
                .get()
                .uri("/api/v1/auth/profile")
                .attribute(AuthService.LOGIN_KEY, "id1")
                .attribute("test", "test")
                .attributes {
                    it["test2"] = "test2"
                }
                .exchange()
                // then
                .expectStatus().isOk
                .expectBody(ApiResponse::class.java)
                .consumeWith { response ->
                    val body = response.responseBody
                    assertThat(body).isNotNull
                    assertThat(body?.success).isTrue()
                    assertThat(body?.data).isNotNull
                    assertThat((body?.data as Map<*, *>)["login"]).isEqualTo("memberId")
                }
        }

        @Test
        @DisplayName("로그인 되지 않은 사용자 프로필 조회 시 null로 응답한다")
        fun testProfileFail() {
            // when
            webTestClient.get()
                .uri("/api/v1/auth/profile")
                .exchange()
                // then
                .expectStatus().isOk
                .expectBody(ApiResponse::class.java)
                .consumeWith { response ->
                    val body = response.responseBody
                    assertThat(body).isNotNull
                    assertThat(body?.success).isTrue()
                    assertThat(body?.data).isEqualTo(null)
                }
        }
    }

    // 세션 정보를 변경하는 WebTestClientConfigurer
    class SessionMutator(
        private val attributes: Map<String, Any>
    ) : WebTestClientConfigurer {

        // Configuerer 추가 시 세션 정보 변경
        override fun afterConfigurerAdded(
            builder: WebTestClient.Builder,
            httpHandlerBuilder: WebHttpHandlerBuilder?,
            connector: ClientHttpConnector?
        ) {
            val sessionMutatorFilter = SessionMutatorFilter(attributes)
            httpHandlerBuilder!!.filters { filters: MutableList<WebFilter?> ->
                filters.add(0, sessionMutatorFilter)
            }
        }

        // 세션 정보 변경하는 WebFilter
        private class SessionMutatorFilter(
            private val attributes: Map<String, Any>
        ) : WebFilter {
            override fun filter(exchange: ServerWebExchange, webFilterChain: WebFilterChain): Mono<Void> {
                return exchange.session
                    .doOnNext { webSession: WebSession ->
                        // attributes에 세션 정보 추가
                        webSession.attributes.putAll(attributes)
                    }.then(
                        webFilterChain.filter(exchange)
                    )
            }
        }
    }
}
```

## Task 11
- 소제목: 서버 및 인프라 배포(AWS)
- 멘토 코멘트: 클라우드에 애플리케이션을 배포해봅니다.
- 소요시간: 180분
- 답변 Type : 장문형

기능 구현과 테스트를 모두 마치셨습니다.
이제 클라우드에 앱을 업로드해 실제로 동작하게 만들어야합니다.

Dockerfile을 이용해 도커 이미지를 만들어 docker.io에 업로드해야합니다.
[docker hub](https://hub.docker.com/) 에 가입을 하고 퍼블릭 레포지토리를 만들어주세요.
[personal-access-tokens](https://app.docker.com/settings/personal-access-tokens) 설정 화면에서 토큰을 생성하고 잘 기록해둡니다.
다운받으신 샘플 코드의 루트 폴더(`/`) 에 `.dockerpassword` 파일을 생성하고 토큰을 저장합니다.
제공드린 스크립트 중 `script/build.sh`, `script/push.sh`를 이용해 이미지를 빌드하고 업로드합니다.

다음으로 AWS EKS에 배포를 해야합니다.
AWS[https://aws.amazon.com/ko/free] 에 가입을 먼저 진행합니다.
그다음 AWS 콘솔의 [create-access-key](https://us-east-1.console.aws.amazon.com/iam/home?region=ap-northeast-2#/users/details/user/create-access-key) 에 접속해 access key를 생성하고 잘 기록해둡니다.
잊어버리셨을 경우 삭제하고 다시 생성해주세요.

[aws cli](https://aws.amazon.com/ko/cli/)를 설치하고 아래 명령을 실행해주세요.
```console
$ aws configure
AWS Access Key ID [None]: (만들었던 Access Key ID 입력)
AWS Secret Access Key [None]: (만들었던 Secret Access Key 입력)
Default region name [None]: ap-northeast-2
Default output format [None]: json
```

먼저 제공드린 스크립트 중 `aws/dynamodb/create-tables.sh`를 이용해 DynamoDB 테이블을 생성합니다.
여태까지 로컬로 작업하던 데이터베이스를 온라인으로 연결합니다.


이제 EKS 클러스터를 생성해야합니다.
먼저 [kubectl](https://kubernetes.io/ko/docs/tasks/tools/) 설치를 진행합니다.
`kubectl version`을 실행해 정상 동작을 확인합니다.

그 다음 EKS 관리 도구인 [eksctl](https://github.com/eksctl-io/eksctl/releases)를 최신 버전으로 다운로드합니다.
필요한 폴더에 넣으신 뒤 `eksctl version`을 실행해 정상 동작을 확인합니다(`~/.bash_profile` 등에 PATH를 설정해두길 권장).

여기까지 되셨다면 제공드린 스크립트 중 `create-cluster.sh`를 이용해 EKS 클러스터를 생성합니다.
생성 완료까지는 시간이 꽤 소요되어서 여유를 가지고 기다려주세요.

배포 진행 전 `chart/values/dev/values.yaml`와 `chart/values/prod/values.yaml`에서
`aws` 키 아래의 FIXME 문자열을 수강자님의 AWS 계정 정보에 맞게 설정해주세요.

생성이 완료되면 `script/helm-upgrade.sh`를 이용해 차트를 배포합니다.
HOST가 할당될 때까지 아래 명령을 통해 상태를 조회합니다.

```sh
kubectl get ingress
```

`No resources found in default namespace.` 문구가 나오면
아직 ingress 객체가 생성되지 않은 것으로 잠시 후에 다시 시도해주세요.
충분히 기다려도 객체가 조회되지 않는다면 `script/helm-delete.sh`를 통해
helm 객체를 삭제하고 `script/helm-upgrade.sh`를 실행해 helm 객체를 재생성해주세요.
정상적으로 ingress 객체가 조회되고 HOST가 할당되었다면
`http://`(s 없음)를 앞에 붙힌 주소를 통해 접속합니다.

로그인 화면이 나온다면 정상입니다.
답안 코드의 경우 test/test의 아이디와 비밀번호로 로그인 가능합니다.

질문: helm은 무엇인가요?

- 정답
Helm은 쿠버네티스 애플리케이션의 패키지 관리 도구입니다. 여러 쿠버네티스 리소스를 차트(Chart)라는 패키지 형식으로 손쉽게 배포, 설치, 업그레이드할 수 있게 해주는 도구입니다.

## Task 12
- 소제목: 성능 테스트
- 멘토 코멘트: 처리 가능한 트래픽을 테스트해봅니다.
- 소요시간: 30분
- 답변 Type : 장문형

Task 2에서 성능 목표를 CPU 50% 상황에서 최소 50 TPS로 정한 것을 복습해봅시다.
AWS에 앱을 배포한 상황에서 실제 트래픽 테스트를 진행할 수 있는데요.
사용할 툴은 [nGrinder](https://github.com/naver/ngrinder)로 오픈소스 서버 부하 테스트 플랫폼입니다.
[releases](https://github.com/naver/ngrinder/releases/)에서 최신 컨트롤러를 받고,
직접 가이드에 따라 실행시키거나
`~/.ngrinder` 폴더를 생성하고 controller의 jar 파일을 복사해두고
제공드린 스크립트 중 `ngrinder.sh`를 실행해 컨트롤러를 실행시킵니다.

실행된 nGrinder를 http://localhost:8099/ 로 접속해 admin/admin으로 로그인하고
우측 상단의 admin 화살표 메뉴를 클릭하고 Download Agent를 한 뒤
`~/.ngrinder` 폴더에 압축을 해제합니다.

그리고 `ngrinder.sh`를 `Ctrl+C`로 종료시키고, 다시 실행합니다.
그 다음 Script에서 Create를 클릭하고 Task 11에서 조회한 ingress url을 http://를 포함해 작성하고,
파일 이름은 ngrinder-worker로 지정합니다.

생성된 스크립트에서 Validate를 진행하고 오류가나지 않는다면 Save/Close를 클릭해 저장합니다.
Performance Test에서 Create를 클릭하고 Vuser 4로 지정후 Save and Start를 클릭해 1분간 테스트를 실행합니다.
TPS가 1이상인 그래프가 기록된다면 정상적으로 실행되고 있는 것입니다.

개발한 시스템을 테스트하고 평균 TPS와 오류율 결과를 확인하고
Task 2에서 목표한 처리량과 확장성에 대해 검토해주세요.

질문: 성능 테스트는 왜 중요한가요?

- 정답: 예상 트래픽 부하에서 시스템이 안정적으로 작동하는지 미리 확인하고 시스템을 수정할 수 있는 기회를 제공하기 때문입니다.