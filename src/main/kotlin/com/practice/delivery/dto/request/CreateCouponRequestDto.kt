package com.practice.delivery.dto.request

import java.time.LocalDate
import javax.validation.constraints.NotNull

data class CreateCouponRequestDto(
    @field:NotNull(message = "할인율은 필수 입력 값입니다.")
    var discountRate: Int,
    @field:NotNull(message = "할인 금액은 필수 입력 값입니다.")
    var discountPrice: Int,
    @field:NotNull(message = "최소 사용 금액은 필수 입력 값입니다.")
    var minSpend: Int,
    @field:NotNull(message = "최대 할인 값은 필수 입력 값입니다.")
    var maxDiscount: Int,
    @field:NotNull(message = "만료일자는 필수 입력 값입니다.")
    var expiryDate: LocalDate,
    @field:NotNull(message = "쿠폰 개수는 필수 입력 값입니다.")
    var quantity: Int
)