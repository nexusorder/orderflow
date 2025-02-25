> 각 설명은 요청부와 응답부로 나뉩니다.<br/>
> 첫 부분이 요청부이고, HTTP/1.1로 시작되는 문단부터 응답부입니다.<br/>
> //로 표시된 주석은 설명이고, 실제 데이터는 {와 } 사이의 json 형식으로 소통합니다.

<br/>

# 인증(auth-controller)
## 회원가입
```http
POST /api/v1/auth/signup HTTP/1.1
Content-Type: application/json
Accept: application/json

{
  "login": "string", // 로그인 아이디
  "password": "string", // 비밀번호(해시)
  "passwordConfirm": "string", // 비밀번호 확인(해시)
  "name": "string", // 이름
  "nickname": "string", // 별명
  "phone": "string", // 전화번호
  "email": "string", // 이메일
  "address": "string", // 주소
  "latitude": 37, // 위도
  "longitude": 126 // 경도
}

HTTP/1.1 200 OK
Content-Type: application/json

{
  "success": true, // 성공 여부(true/false)
  "data": true, // 비즈니스 데이터(true: 성공, false: 실패)
  "errorCode": null, // 오류 코드(int | null)
  "errorMessage": null // 오류 메시지(string | null)
}
```

## 로그인
```http
POST /api/v1/auth/login HTTP/1.1
Content-Type: application/json
Accept: application/json

{
  "login": "string", // 로그인 아이디
  "password": "string" // 비밀번호(해시)
}

HTTP/1.1 200 OK
Content-Type: application/json

{
  "success": true,
  "data": true,
  "errorCode": null,
  "errorMessage": null
}
```

## 로그아웃
```http
POST /api/v1/auth/logout HTTP/1.1
Accept: application/json

HTTP/1.1 200 OK
Content-Type: application/json

{
  "success": true,
  "data": true,
  "errorCode": null,
  "errorMessage": null
}
```

## 내 정보 조회
```http
GET /api/v1/auth/profile HTTP/1.1
Accept: application/json

HTTP/1.1 200 OK
Content-Type: application/json

{
  "success": true, // 성공 여부
  "data": {
    "login": "string", // 로그인 아이디
    "name": "string", // 이름
    "nickname": "string", // 별명
    "email": "string", // 이메일
    "phone": "string", // 전화번호
    "address": "string", // 주소
    "latitude": 0, // 위도
    "longitude": 0, // 경도
    "seller": true // 판매자 여부
  },
  "errorCode": null, // 오류 코드
  "errorMessage": null // 오류 메시지
}
```

<br/>

# 카테고리(category-controller)
## 카테고리 조회
```http
GET /api/v1/categories HTTP/1.1
Accept: application/json

HTTP/1.1 200 OK
Content-Type: application/json
{
  "success": true,
  "data": [
    {
      "key": "string", // 카테고리 키
      "name": "string", // 카테고리 이름
      "imageUrl": "string", // 카테고리 이미지 URL
      "enabled": true, // 활성화 여부
      "order": 0 // 배치 순서
    }
  ],
  "errorCode": null,
  "errorMessage": null
}
```

<br/>

# 가게(shop-controller)
## 가게 전체 조회
```http
GET /api/v1/shops HTTP/1.1
Accept: application/json

HTTP/1.1 200 OK
Content-Type: application/json
{
  "success": true,
  "data": [
    {
      "id": "string", // 가게 ID
      "name": "string", // 상호명
      "address": "string", // 가게 주소
      "phone": "string", // 가게 전화번호
      "email": "string", // 가게 이메일
      "imageUrl": "string", // 가게 이미지 URL
      "isOpened": true, // 영업중 여부
      "enabled": true, // 활성화 여부(판매자 제어용)
      "openTime": "string", // 영업 시작 시간
      "closeTime": "string", // 영업 종료 시간
      "category": "string", // 카테고리
      "products": [ // 메뉴 목록
        {
          "shopId": "string", // 가게 ID
          "name": "string", // 메뉴명
          "description": "string", // 메뉴 설명
          "price": 0, // 가격(원 단위)
          "category": "string", // 카테고리
          "imageUrl": "string", // 메뉴 이미지 URL
          "enabled": true, // 활성화 여부(판매자 제어용)
          "id": "string" // 상품 ID
        }
      ],
      "minimumOrder": 0, // 최수 주문 금액(원 단위)
      "deliveryFee": 0, // 배달 수수료(원 단위)
      "rating": 0, // 평점
      "latitude": 0, // 위도
      "longitude": 0 // 경도
    }
  ],
  "errorCode": null,
  "errorMessage": null
}
```

## 가게 상세 조회
```http
GET /api/v1/shops/{id} HTTP/1.1
Accept: application/json

HTTP/1.1 200 OK
Content-Type: application/json
{
  "success": true,
  "data": {
    "id": "string", // 가게 ID
    "name": "string", // 상호명
    "address": "string", // 주소
    "phone": "string", // 전화번호
    "email": "string", // 이메일
    "imageUrl": "string", // 가게 이미지 URL
    "isOpened": true, // 가게 영업 여부
    "enabled": true, // 활성화 여부(판매자 제어용)
    "openTime": "string", // 영업 시작 시간
    "closeTime": "string", // 영업 종료 시간
    "category": "string", // 카테고리
    "products": [ // 메뉴 목록
      {
        "shopId": "string", // 가게 ID
        "name": "string", // 메뉴명
        "description": "string", // 메뉴 설명
        "price": 0, // 가격(원 단위)
        "category": "string", // 카테고리
        "imageUrl": "string", // 메뉴 이미지 URL
        "enabled": true, // 활성화 여부(판매자 제어용)
        "id": "string" // 메뉴 아이디
      }
    ],
    "minimumOrder": 0, // 최소 주문 금액(원 단위)
    "deliveryFee": 0, // 배달 수수료(원 단위)
    "rating": 0, // 평점
    "latitude": 0, // 위도
    "longitude": 0 // 경도
  },
  "errorCode": null,
  "errorMessage": null
}
```

<br/>

# 검색(search-controller)
## 가게 및 상품 검색
```http
POST /api/v1/search HTTP/1.1
Accept: application/json

{
    "name": "양념",
    "category": ""
}

HTTP/1.1 200 OK
Content-Type: application/json

{
  "success": true,
  "data": [ // 가게 상세 응답 모델과 동일
    {
      "id": "string",
      "name": "string",
      "address": "string",
      "phone": "string",
      "email": "string",
      "imageUrl": "string",
      "isOpened": true,
      "enabled": true,
      "openTime": "string",
      "closeTime": "string",
      "category": "string",
      "products": [
        {
          "shopId": "string",
          "name": "string",
          "description": "string",
          "price": 0,
          "category": "string",
          "imageUrl": "string",
          "enabled": true,
          "id": "string"
        }
      ],
      "minimumOrder": 0,
      "deliveryFee": 0,
      "rating": 0,
      "latitude": 0,
      "longitude": 0
    }
  ],
  "errorCode": null,
  "errorMessage": null
}
```

<br/>

# 주문(order-controller)
## 주문 목록 조회
```http
GET /api/v1/orders HTTP/1.1
Accept: application/json

HTTP/1.1 200 OK
Content-Type: application/json
{
  "success": true,
  "data": [
    {
      "memberId": "string", // 회원 ID
      "shop": {
        "id": "string",
        "name": "string",
        "address": "string",
        "phone": "string",
        "email": "string",
        "imageUrl": "string",
        "isOpened": true,
        "enabled": true,
        "openTime": "string",
        "closeTime": "string",
        "category": "string",
        "products": [
          {
            "shopId": "string",
            "name": "string",
            "description": "string",
            "price": 0,
            "category": "string",
            "imageUrl": "string",
            "enabled": true,
            "id": "string"
          }
        ],
        "minimumOrder": 0,
        "deliveryFee": 0,
        "rating": 0,
        "latitude": 0,
        "longitude": 0
      },
      "products": [
        {
          "shopId": "string",
          "name": "string",
          "description": "string",
          "price": 0,
          "category": "string",
          "imageUrl": "string",
          "enabled": true,
          "id": "string",
          "quantity": 0
        }
      ],
      "status": "PENDING", // 주문 상태
      "reviewId": "string", // 리뷰 ID(FK)
      "deliveryAddress": "string", // 배달 주소
      "deliveryPhone": "string", // 배달 전화번호
      "deliveredTime": "string", // 배달 소요 시간
      "deliveryFee": 0, // 배달 수수료
      "grandTotal": 0, // 총 금액
      "paymentMethod": "EMPTY", // 결제 방법
      "paymentStatus": "PENDING", // 결제 상태
      "deliveryStatus": "PENDING", // 배달 상태
      "refund": { // 환불 정보
        "refundStatus": "PENDING", // 환불 상태
        "refundReason": "string", // 환불 사유
        "refundRequestedDatetime": "string", // 환불 요청 시각
        "refundCompletedDatetime": "string", // 환불 완료 시각
        "refundPaymentMethod": "EMPTY", // 환불 결제 수단
        "refundAmount": 0 // 환불 금액
      },
      "id": "string", // 주문 ID
      "createdAt": "string" // 주문 생성 시각
    }
  ],
  "errorCode": null,
  "errorMessage": null
}
```

## 주문 생성
```http
POST /api/v1/orders HTTP/1.1
Content-Type: application/json
Accept: application/json

{
  "shopId": "string", // 가게 ID
  "products": [ // 주문 대상 상품 목록
    {
      "productId": "string", // 상품 ID
      "quantity": 0 // 상품 수량
    }
  ]
}

HTTP/1.1 200 OK
Content-Type: application/json

{
  "success": true,
  "data": "orderId", // 주문 ID
  "errorCode": null,
  "errorMessage": null
}
```

## 주문 상세 조회
```http
GET /api/v1/orders/{id} HTTP/1.1
Accept: application/json

HTTP/1.1 200 OK
Content-Type: application/json

{
  "success": true,
  "data": {
    "memberId": "string",
    "shop": {
      "id": "string",
      "name": "string",
      "address": "string",
      "phone": "string",
      "email": "string",
      "imageUrl": "string",
      "isOpened": true,
      "enabled": true,
      "openTime": "string",
      "closeTime": "string",
      "category": "string",
      "products": [
        {
          "shopId": "string",
          "name": "string",
          "description": "string",
          "price": 0,
          "category": "string",
          "imageUrl": "string",
          "enabled": true,
          "id": "string"
        }
      ],
      "minimumOrder": 0,
      "deliveryFee": 0,
      "rating": 0,
      "latitude": 0,
      "longitude": 0
    },
    "products": [
      {
        "shopId": "string",
        "name": "string",
        "description": "string",
        "price": 0,
        "category": "string",
        "imageUrl": "string",
        "enabled": true,
        "id": "string",
        "quantity": 0
      }
    ],
    "status": "PENDING",
    "reviewId": "string",
    "deliveryAddress": "string",
    "deliveryPhone": "string",
    "deliveredTime": "string",
    "deliveryFee": 0,
    "grandTotal": 0,
    "paymentMethod": "EMPTY",
    "paymentStatus": "PENDING",
    "deliveryStatus": "PENDING",
    "refund": {
      "refundStatus": "PENDING",
      "refundReason": "string",
      "refundRequestedDatetime": "string",
      "refundCompletedDatetime": "string",
      "refundPaymentMethod": "EMPTY",
      "refundAmount": 0
    },
    "id": "string",
    "createdAt": "string"
  },
  "errorCode": null,
  "errorMessage": null
}
```
