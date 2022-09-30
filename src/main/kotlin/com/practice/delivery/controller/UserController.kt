package com.practice.delivery.controller

import com.practice.delivery.dto.request.RegisterUserRequestDto
import com.practice.delivery.dto.response.RegisterUserResponseDto
import com.practice.delivery.entity.Role
import com.practice.delivery.service.UserService
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/user")
class UserController(private var userService: UserService) {
    @PostMapping("/register")
    fun register(@Valid @RequestBody req:RegisterUserRequestDto,bindingResult: BindingResult):RegisterUserResponseDto {
//        if (req.role == Role.ROLE_DEFAULT) {
            return userService.registerDefaultUser(req,bindingResult)
//        }
    }
}