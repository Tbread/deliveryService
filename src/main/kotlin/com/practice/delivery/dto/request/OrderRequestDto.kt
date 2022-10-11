package com.practice.delivery.dto.request

import javax.validation.constraints.NotNull

data class OrderRequestDto(
    @field:NotNull(message = "메뉴 ID리스트는 필수 값입니다.")
    val menuList:List<Long>,
    @field:NotNull(message = "가게 ID는 필수 값입니다.")
    val storeId:Long,
    @field:NotNull(message = "주문 개수는 필수 값입니다.")
    val quantityList:List<Int>,
    val couponId:Long?
)