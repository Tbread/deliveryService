package com.practice.delivery.dto.request

import com.practice.delivery.model.OptionMenu
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class AddMenuRequestDto (
    @field:NotBlank(message = "메뉴 이름은 필수 값입니다.")
    val menuName:String?,
    val desc:String?,
    @field:NotNull(message = "메뉴 가격은 필수 값입니다.")
    val price:Int?,
    val imgSrc:String?,
    @field:NotNull(message = "옵션여부는 필수 값입니다.")
    val hasOption:Boolean?,
    val optionMenuList: List<OptionMenu>?

    )