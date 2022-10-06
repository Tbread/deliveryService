package com.practice.delivery.service

import com.practice.delivery.dto.request.RegisterStoreRequestDto
import com.practice.delivery.dto.response.RegisterStoreResponseDto
import com.practice.delivery.service.Implement.UserDetailsImpl
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.BindingResult

interface StoreService {

    fun registerStore(@AuthenticationPrincipal userDetails: UserDetailsImpl,req: RegisterStoreRequestDto,bindingResult: BindingResult):RegisterStoreResponseDto

    fun viewRegisterStoreRequestList(@AuthenticationPrincipal userDetails: UserDetailsImpl):Any

    fun acceptRegisterStoreRequest(@AuthenticationPrincipal userDetails: UserDetailsImpl,id:Long):Any

    fun denyRegisterStoreRequest(@AuthenticationPrincipal userDetails: UserDetailsImpl,id:Long):Any
}