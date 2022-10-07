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
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/user")
class UserController(private var userService: UserService) {
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

    @PostMapping("/login")
    fun login(@Valid @RequestBody req: LoginRequestDto, bindingResult: BindingResult): LoginResponseDto {
        return userService.login(req, bindingResult)
    }

    @GetMapping("/register-admin-request-list")
    fun viewRegisterAdminRequestList(@AuthenticationPrincipal userDetails: UserDetailsImpl): ViewRegisterAdminRequestListResponseDto {
        return userService.viewRegisterAdminList(userDetails)
    }

    @GetMapping("/accept-admin-request/{id}")
    fun acceptRegisterAdmin(@AuthenticationPrincipal userDetails: UserDetailsImpl, @PathVariable id: Long): ManageRegisterAdminResponseDto {
        return userService.acceptRegisterAdmin(userDetails, id)
    }

    @GetMapping("/deny-admin-request/{id}")
    fun denyRegisterAdmin(@AuthenticationPrincipal userDetails: UserDetailsImpl, @PathVariable id: Long): ManageRegisterAdminResponseDto {
        return userService.denyRegisterAdmin(userDetails, id)
    }
}