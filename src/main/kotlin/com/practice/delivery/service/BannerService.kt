package com.practice.delivery.service

import com.practice.delivery.dto.request.AddBannerRequestDto
import com.practice.delivery.dto.response.AddBannerResponseDto
import com.practice.delivery.service.implemented.UserDetailsImpl
import org.springframework.validation.BindingResult

interface BannerService {

    fun addBanner(
        userDetails: UserDetailsImpl,
        req: AddBannerRequestDto,
        bindingResult: BindingResult
    ): AddBannerResponseDto
}