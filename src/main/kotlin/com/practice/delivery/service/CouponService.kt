package com.practice.delivery.service

import com.practice.delivery.dto.request.CreateCouponRequestDto
import com.practice.delivery.dto.response.CreateCouponResponseDto
import com.practice.delivery.dto.response.DefaultResponseDto
import com.practice.delivery.service.implemented.UserDetailsImpl
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.BindingResult

interface CouponService {

    fun createCoupon(userDetails: UserDetailsImpl,req:CreateCouponRequestDto,bindingResult: BindingResult):CreateCouponResponseDto

    fun couponIssuance(userDetails: UserDetailsImpl,id:Long):DefaultResponseDto
}