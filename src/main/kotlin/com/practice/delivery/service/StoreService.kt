package com.practice.delivery.service

import com.practice.delivery.dto.request.RegisterStoreRequestDto
import com.practice.delivery.dto.response.ManageRegisterStoreResponseDto
import com.practice.delivery.dto.response.RegisterStoreResponseDto
import com.practice.delivery.dto.response.ViewRegisterStoreRequestListResponseDto
import com.practice.delivery.service.implement.UserDetailsImpl
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.BindingResult

interface StoreService {

    fun registerStore(@AuthenticationPrincipal userDetails: UserDetailsImpl, req: RegisterStoreRequestDto, bindingResult: BindingResult):RegisterStoreResponseDto

    fun viewRegisterStoreRequestList(@AuthenticationPrincipal userDetails: UserDetailsImpl): ViewRegisterStoreRequestListResponseDto

    fun acceptRegisterStoreRequest(@AuthenticationPrincipal userDetails: UserDetailsImpl, id:Long): ManageRegisterStoreResponseDto

    fun denyRegisterStoreRequest(@AuthenticationPrincipal userDetails: UserDetailsImpl, id:Long):ManageRegisterStoreResponseDto
}