package com.practice.delivery.dto.request

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class RegisterStoreRequestDto (
    @field:NotNull(message = "가게 이름은 필수 값입니다.")
    @field:NotBlank(message = "가게 이름은 공백이 될 수 없습니다.")
    val storeName:String,
    val storeDesc:String?,
    val storeImgSrc:String?,
    val minOrderPrice:Int?
        )