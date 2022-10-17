package com.practice.delivery.dto.request

import java.time.LocalDate
import javax.validation.constraints.NotNull

data class AddBannerRequestDto(
    @field:NotNull(message = "배너 이미지 링크는 필수 값입니다.")
    val bannerImgSrc: String,
    @field:NotNull(message = "배너 링크는 필수 값입니다.")
    val bannerSrc: String,
    @field:NotNull(message = "만료 일자는 필수 값입니다.")
    val expireDate: LocalDate
)