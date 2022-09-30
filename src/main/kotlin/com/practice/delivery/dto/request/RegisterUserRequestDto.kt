package com.practice.delivery.dto.request

import com.practice.delivery.entity.Role
import javax.validation.constraints.NotNull

data class RegisterUserRequestDto(

    @field:NotNull(message = "이메일은 필수 입력 값입니다.")
    val email:String?,

    @field:NotNull(message = "비밀번호는 필수 입력 값입니다.")
    val password:String?,

    @field:NotNull(message = "회원종류는 필수 입력 값입니다.")
    val role:Role?

)
