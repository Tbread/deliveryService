package com.practice.delivery.service

import com.practice.delivery.dto.request.AddMenuRequestDto
import com.practice.delivery.dto.response.AddMenuResponseDto
import com.practice.delivery.service.implement.UserDetailsImpl
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.BindingResult

interface MenuService {

    fun addMenu(@AuthenticationPrincipal userDetails: UserDetailsImpl,req:AddMenuRequestDto,bindingResult: BindingResult):AddMenuResponseDto
}