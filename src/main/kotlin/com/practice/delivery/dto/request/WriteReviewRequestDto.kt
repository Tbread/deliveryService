package com.practice.delivery.dto.request

import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class WriteReviewRequestDto(
    @field:NotNull(message = "주문 ID는 필수 값입니다.")
    val orderId: Long,
    @field:NotBlank(message = "리뷰 내용은 필수 값입니다.")
    val content: String,
    val imgSrc: String?,
    @field:NotNull(message = "점수는 필수 값입니다.")
    @field:Min(value = 1, message = "점수는 1~5사이의 정수값이여야합니다.")
    @field:Max(value = 5, message = "점수는 1~5사이의 정수값이여야합니다.")
    val score: Int
)