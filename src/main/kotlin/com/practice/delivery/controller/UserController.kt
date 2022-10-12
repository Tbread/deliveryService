package com.practice.delivery.controller

import com.practice.delivery.dto.request.LoginRequestDto
import com.practice.delivery.dto.request.RegisterUserRequestDto
import com.practice.delivery.dto.response.LoginResponseDto
import com.practice.delivery.dto.response.ManageRegisterAdminResponseDto
import com.practice.delivery.dto.response.RegisterUserResponseDto
import com.practice.delivery.dto.response.ViewRegisterAdminRequestListResponseDto
import com.practice.delivery.entity.Role
import com.practice.delivery.service.implemented.UserDetailsImpl
import com.practice.delivery.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@Tag(name = "유저 인증")
@RestController
@RequestMapping("/user")
class UserController(private var userService: UserService) {

    @Operation(summary = "회원가입 API")
    @PostMapping("/register")
    fun register(
        @Valid @RequestBody req: RegisterUserRequestDto,
        bindingResult: BindingResult
    ): RegisterUserResponseDto {
        return when (req.role) {
            Role.DEFAULT -> {
                userService.registerDefaultAndBusinessUser(req, bindingResult)
            }
            Role.BUSINESS -> {
                userService.registerDefaultAndBusinessUser(req, bindingResult)
            }
            else -> {
                userService.registerAdminUser(req, bindingResult)
            }
        }
    }


    @Operation(summary = "로그인 API")
    @PostMapping("/login")
    fun login(@Valid @RequestBody req: LoginRequestDto, bindingResult: BindingResult): LoginResponseDto {
        return userService.login(req, bindingResult)
    }

    @Operation(summary = "관리자 가입 신청 리스트 조회 API")
    @GetMapping("/register-admin-request-list")
    fun viewRegisterAdminRequestList(@AuthenticationPrincipal userDetails: UserDetailsImpl): ViewRegisterAdminRequestListResponseDto {
        return userService.viewRegisterAdminList(userDetails)
    }

    @Operation(summary = "관리자 가입 신청 수락 API")
    @GetMapping("/accept-admin-request/{id}")
    fun acceptRegisterAdmin(@AuthenticationPrincipal userDetails: UserDetailsImpl, @PathVariable id: Long): ManageRegisterAdminResponseDto {
        return userService.acceptRegisterAdmin(userDetails, id)
    }

    @Operation(summary = "관리자 가입 신청 거절 API")
    @GetMapping("/deny-admin-request/{id}")
    fun denyRegisterAdmin(@AuthenticationPrincipal userDetails: UserDetailsImpl, @PathVariable id: Long): ManageRegisterAdminResponseDto {
        return userService.denyRegisterAdmin(userDetails, id)
    }
}