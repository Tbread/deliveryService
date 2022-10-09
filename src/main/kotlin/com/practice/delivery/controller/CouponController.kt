package com.practice.delivery.controller

import com.practice.delivery.dto.request.CreateCouponRequestDto
import com.practice.delivery.dto.response.CreateCouponResponseDto
import com.practice.delivery.dto.response.DefaultResponseDto
import com.practice.delivery.service.CouponService
import com.practice.delivery.service.implemented.UserDetailsImpl
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/coupon")
class CouponController(private var couponService: CouponService) {

    @PostMapping("/create")
    fun createCoupon(
        @AuthenticationPrincipal userDetails: UserDetailsImpl,
        @RequestBody @Valid req: CreateCouponRequestDto,
        bindingResult: BindingResult
    ): CreateCouponResponseDto {
        return couponService.createCoupon(userDetails, req, bindingResult)
    }

    @GetMapping("/issuance/{id}")
    fun issuanceCoupon(
        @AuthenticationPrincipal userDetails: UserDetailsImpl,
        @PathVariable id: Long
    ): DefaultResponseDto {
        return couponService.couponIssuance(userDetails, id)
    }
}