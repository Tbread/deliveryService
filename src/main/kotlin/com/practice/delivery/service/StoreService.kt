package com.practice.delivery.service

import com.practice.delivery.dto.request.RegisterStoreRequestDto
import com.practice.delivery.dto.request.UpdateStoreRequestDto
import com.practice.delivery.dto.response.*
import com.practice.delivery.service.implemented.UserDetailsImpl
import org.springframework.validation.BindingResult

interface StoreService {

    fun registerStore(
        userDetails: UserDetailsImpl,
        req: RegisterStoreRequestDto,
        bindingResult: BindingResult
    ): RegisterStoreResponseDto

    fun viewRegisterStoreRequestList(
        userDetails: UserDetailsImpl,
        statusCode: Int?
    ): ViewRegisterStoreRequestListResponseDto

    fun acceptRegisterStoreRequest(userDetails: UserDetailsImpl, id: Long): ManageRegisterStoreResponseDto

    fun denyRegisterStoreRequest(userDetails: UserDetailsImpl, id: Long): ManageRegisterStoreResponseDto

    fun updateStoreInfo(
        userDetails: UserDetailsImpl,
        req: UpdateStoreRequestDto,
        bindingResult: BindingResult
    ): DefaultResponseDto

    fun manageFavorStore(userDetails: UserDetailsImpl, id: Long): DefaultResponseDto

    fun viewFavorStoreList(userDetails:UserDetailsImpl):ViewFavorStoreResponseDto
}