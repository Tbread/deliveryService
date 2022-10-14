package com.practice.delivery.controller

import com.practice.delivery.dto.request.LoginRequestDto
import com.practice.delivery.dto.request.RegisterUserRequestDto
import com.practice.delivery.dto.response.LoginResponseDto
import com.practice.delivery.dto.response.ManageRegisterAdminResponseDto
import com.practice.delivery.dto.response.RegisterUserResponseDto
import com.practice.delivery.dto.response.ViewRegisterAdminRequestListResponseDto
import com.practice.delivery.entity.Role
import com.practice.delivery.service.UserService
import com.practice.delivery.service.implemented.UserDetailsImpl
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
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

    @Operation(summary = "관리자 가입 신청 리스트 조회 API", description = "statusCode: 0=대기중,1=수락됨,2=거절됨을 뜻합니다.<br />입력하지 않거나 잘못된 입력은 모든 정보를 가져옵니다.<br /> 헤더에 Authorization 으로 JWT 토큰을 요구합니다.")
    @GetMapping("/register-admin-request-list")
    fun viewRegisterAdminRequestList(@Parameter(hidden = true) @AuthenticationPrincipal userDetails: UserDetailsImpl,@RequestParam(required = false, value = "statusCode") statusCode:Int?): ViewRegisterAdminRequestListResponseDto {
        return userService.viewRegisterAdminList(userDetails,statusCode)
    }

    @Operation(summary = "관리자 가입 신청 수락 API", description = "헤더에 Authorization 으로 JWT 토큰을 요구합니다.")
    @GetMapping("/accept-admin-request/{id}")
    fun acceptRegisterAdmin(
        @Parameter(hidden = true) @AuthenticationPrincipal userDetails: UserDetailsImpl,
        @PathVariable id: Long
    ): ManageRegisterAdminResponseDto {
        return userService.acceptRegisterAdmin(userDetails, id)
    }

    @Operation(summary = "관리자 가입 신청 거절 API", description = "헤더에 Authorization 으로 JWT 토큰을 요구합니다.")
    @GetMapping("/deny-admin-request/{id}")
    fun denyRegisterAdmin(
        @Parameter(hidden = true) @AuthenticationPrincipal userDetails: UserDetailsImpl,
        @PathVariable id: Long
    ): ManageRegisterAdminResponseDto {
        return userService.denyRegisterAdmin(userDetails, id)
    }
}