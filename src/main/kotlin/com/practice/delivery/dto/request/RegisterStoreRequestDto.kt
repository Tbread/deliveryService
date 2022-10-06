package com.practice.delivery.dto.request

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class RegisterStoreRequestDto (
    @field:NotBlank(message = "가게 이름은 필수 값입니다.")
    val storeName:String?,
    val storeDesc:String?,
    val storeImgSrc:String?,
    val minOrderPrice:Int?
        )