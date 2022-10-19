# 배달앱 - Backend

Link : http://aws.tbread-delivery.shop (뷰 존재하지 않음)

Project Trouble Shooting Notion : https://trite-deal-ed5.notion.site/Backend-e8d843f5516c4ab39641e56310d7addb

Swagger Ui Link : http://aws.tbread-delivery.shop/swagger-ui/index.html


####
# 개발 환경

Server

- `AWS EC2(Amazon Linux 2)`

DB

- `AWS RDS(MySQL)`
- `Amazon ElastiCache(Redis)`

SCM

- `Git(GitHub)`

BackEnd(Language & Framework & Library)

- `Kotlin 1.7`
- `Java 8`
- `JDK 1.8.0`
- Framework : `Spring Boot 2.7.4`
- IDE : `IntelliJ IDEA`
- Build Tools : `Gradle 7.5`
- ORM : `Spring Data JPA` `QueryDSL` `Redis`
- `JWT`
- `OpenApi 3.1`
- `Spring Sequrity`
- `Jasypt`

### Tech Structure

![아키텍처](https://raw.githubusercontent.com/Tbread/deliveryService/master/readmeImg/structure.png)


# ERD

![edit_ERD](https://raw.githubusercontent.com/Tbread/deliveryService/master/readmeImg/ERD.png)


# DB설계

## User (유저)

    -id (pk,Long)
    -email (이메일,String)
    -password (패스워드,String)
    -lastLoginDate (마지막 로그인 날짜,LocalDateTime)
    -lastOrderDate (마지막 주문 날짜,LocalDate)
    -Role (유저 권한,Enum.String)

## AdminUserRequest (관리자 가입 신청)

    -id (pk,Long)
    -email (이메일,String)
    -password (패스워드,String)
    -status (신청상태,Enum.String)
    -approver (해당 신청의 거절/수락자,User)

## Store (가게)

    -id (pk,Long)
    -storeName (가게 이름,String)
    -storeDesc (가게 설명,String)
    -storeImgSrc (가게 소개 이미지 경로,String)
    -owner (가게 점주,User)
    -minOrderPrice (최소 주문 금액,Int)
    -score (가게 점수,Float)

## StoreRegisterRequest (가게 등록 신청)

    -id (pk,Long)
    -storeName (가게 이름,String)
    -storeDesc (가게 설명,String)
    -storeImgSrc (가게 소개 이미지 경로,String)
    -owner (가게 점주,User)
    -minOrderPrice (최소 주문 금액,Int)
    -status (신청상태,Enum.String)
    -approver (해당 신청의 거절/수락자,User)

## FavorStore (즐겨찾기 가게)
    
    -id (pk,Long)
    -user (즐겨찾기를 등록한 유저,User)
    -store (즐켜찾기에 등록한 가게,Store)

## Menu (메뉴)

    -id (pk,Long)
    -menuName (메뉴명,String)
    -desc (메뉴 설명,String)
    -price (메뉴 가격,Int)
    -imgSrc (메뉴 이미지 링크,String)
    -thisHasOption (옵션메뉴 보유 여부,Boolean)
    -thisIsOption (옵션메뉴 여부,Boolean)
    -thisIsSoldOut (품절 여부,Boolean)
    -store (해당 메뉴 등록 가게,Store)

## MenuOption (옵션메뉴)

    -id (pk,Long)
    -topMenu (옵션을 보유한 메뉴,Menu)
    -subMenu (옵션인 메뉴,Menu)

## MasterCoupon (쿠폰)

    -id (pk,Long)
    -discountRate (할인률,Int)
    -discountPrice (할인가격,Int)
    -minSpend (최소 사용가능 금액,Int) 
    -maxDiscount (최대 할인 금액,Int)
    -expired (만료 여부,Boolean)
    -issuer (쿠폰 발급자,User)
    -expiryDate (쿠폰 만료 날짜,LocalDate)
    -quantity (발급 최대 수량,Int)

## Coupon (발급된 쿠폰)

    -id (pk,Long)
    -expired (만료여부,Boolean)
    -masterCoupon (쿠폰 종류,MasterCoupon)
    -available (사용가능여부,Boolean)
    -owner (해당 쿠폰의 소유 유저,User)

## CouponIssuanceLog (쿠폰 발급 로그)

    -id (pk,Long)
    -masterCoupon (발급받은 쿠폰 종류,MasterCoupon)
    -user (발급받은 유저,User)

## DeliveryOrder (주문)

    -id (pk,Long)
    -orderer (주문자,User)
    -status (주문 상태,Enum.String)
    -usedCoupon (사용된 쿠폰,Coupon)
    -initialPrice (할인 미적용가,Int)
    -finalPrice (할인 적용가,Int)
    -store (주문받은 가게,Store)
    -reviewed (리뷰 작성 여부,Boolean)

## OrderedMenu (주문한 메뉴)

    -id (pk,Long)
    -deliveryOrder (주문건,DeliveryOrder)
    -menu (주문한 메뉴,Menu)
    -quantity (주문 수량,Int)

## Review (리뷰)

    -id (pk,Long)
    -user (리뷰를 단 유저,User)
    -store (리뷰가 작성된 가게,Store)
    -deliveryOrder (리뷰 작성 주문건,DeliveryOrder)
    -contents (리뷰 내용,String)
    -imgSrc (리뷰 이미지 링크,String)
    -score (리뷰 점수,Int)
    -deleted (리뷰 삭제 여부,Boolean)

## Banner (배너)

    -id (pk,Long)
    -imgSrc (이미지 링크,String)
    -src (해당 배너 클릭시 이동시킬 링크,String)
    -user (해당 배너를 추가한 관리자,User)
    -expired (배너 만료 여부,Boolean)
    -expireDate (배너 만료 날짜,LocalDate)




# API 외 기능

## BannerScheduler

    매 0시마다 만료일자를 넘긴 배너들을 만료처리합니다.

## CouponMasterScheduler

    매 0시마다 만료일자를 넘긴 쿠폰을 만료처리하고
    발급된 쿠폰들 또한 사용불가 처리합니다.

## StoreScoreScheduler

    매 0시마다 가게들의 점수를 최근 7일간 작성된 리뷰들을 기준으로 집계합니다

## CustomInterceptor

    API 요청시 컨트롤러 핸들러 호출전에 해당 API를 요청한 클라이언트의 IP를 가져오고
    Redis에 해당 IP와 최근 5초내 요청횟수 키셋을 저장합니다
    해당 키셋의 만료시간은 5초이며 5초 이내에 3회 이상의 요청이 발생시
    Redis에 해당 IP를 키값으로 5분의 만료 시간을 가진 차단 정보를 저장하며
    해당 차단정보가 만료되기 전까지는 API 요청을 할 수 없습니다.


## EnumValidator

    DTO로 Enum 정보를 받아올때 해당 정보의 유효성을 검증합니다
    개발 방향 변경으로 더이상 사용되지 않습니다

## Jasypt

    Jasypt로 application.properties에 사용되는 민감 정보들을 암호화하여
    github -> Jenkins 자동 빌드 과정에서 해당 정보들이 탈취되지 않도록 하였습니다.

