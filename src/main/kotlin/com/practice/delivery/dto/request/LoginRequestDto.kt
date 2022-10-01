package com.practice.delivery.dto.request

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class LoginRequestDto(
    @field:NotNull(message = "이메일은 필수 입력 값입니다.")
    @field:NotBlank(message = "이메일은 필수 입력 값입니다.")
    @field:Email(message = "유효하지 않은 이메일형식입니다.")
    val email: String?,
    @field:NotNull(message = "비밀번호는 필수 입력 값입니다.")
    @field:NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    val password: String?
)