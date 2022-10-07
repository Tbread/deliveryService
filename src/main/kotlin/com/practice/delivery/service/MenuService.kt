package com.practice.delivery.service

import com.practice.delivery.dto.request.AddMenuRequestDto
import com.practice.delivery.dto.response.AddMenuResponseDto
import com.practice.delivery.service.Implement.UserDetailsImpl
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.RequestBody
import javax.validation.Valid

interface MenuService {

    fun addMenu(@AuthenticationPrincipal userDetails: UserDetailsImpl,@RequestBody @Valid req:AddMenuRequestDto,bindingResult: BindingResult):AddMenuResponseDto
}