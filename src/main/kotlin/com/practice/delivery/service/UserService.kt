package com.practice.delivery.service

import com.practice.delivery.dto.request.RegisterUserRequestDto
import com.practice.delivery.dto.response.RegisterUserResponseDto
import org.springframework.stereotype.Service
import org.springframework.validation.BindingResult

interface UserService {

    fun registerDefaultUser(req: RegisterUserRequestDto, bindingResult: BindingResult): RegisterUserResponseDto

    fun registerBusinessUser(req: RegisterUserRequestDto, bindingResult: BindingResult): RegisterUserResponseDto

    fun registerAdminUser(req: RegisterUserRequestDto, bindingResult: BindingResult): RegisterUserResponseDto
}