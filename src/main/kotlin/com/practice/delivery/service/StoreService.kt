package com.practice.delivery.service

import com.practice.delivery.dto.request.RegisterStoreRequestDto
import com.practice.delivery.dto.response.ManageRegisterStoreResponseDto
import com.practice.delivery.dto.response.RegisterStoreResponseDto
import com.practice.delivery.dto.response.ViewRegisterStoreRequestListResponseDto
import com.practice.delivery.service.implemented.UserDetailsImpl
import org.springframework.validation.BindingResult

interface StoreService {

    fun registerStore(userDetails: UserDetailsImpl, req: RegisterStoreRequestDto, bindingResult: BindingResult):RegisterStoreResponseDto

    fun viewRegisterStoreRequestList(userDetails: UserDetailsImpl): ViewRegisterStoreRequestListResponseDto

    fun acceptRegisterStoreRequest(userDetails: UserDetailsImpl, id:Long): ManageRegisterStoreResponseDto

    fun denyRegisterStoreRequest(userDetails: UserDetailsImpl, id:Long):ManageRegisterStoreResponseDto
}