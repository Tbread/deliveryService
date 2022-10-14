package com.practice.delivery.controller

import com.practice.delivery.dto.request.CreateCouponRequestDto
import com.practice.delivery.dto.response.CreateCouponResponseDto
import com.practice.delivery.dto.response.DefaultResponseDto
import com.practice.delivery.service.CouponService
import com.practice.delivery.service.implemented.UserDetailsImpl
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@Tag(name = "쿠폰 관련")
@RestController
@RequestMapping("/coupon")
class CouponController(private var couponService: CouponService) {

    @Operation(
        summary = "쿠폰 등록 API",
        description = "새로운 쿠폰을 정의하여 등록합니다. 해당 쿠폰은 만료일자까지 또는 정해진 수량만큼 쿠폰 발급 API 를 통해 받을 수 있습니다.\n헤더에 Authorization 으로 JWT 토큰을 요구합니다."
    )
    @PostMapping("/create")
    fun createCoupon(
        @Parameter(hidden = true) @AuthenticationPrincipal userDetails: UserDetailsImpl,
        @RequestBody @Valid req: CreateCouponRequestDto,
        bindingResult: BindingResult
    ): CreateCouponResponseDto {
        return couponService.createCoupon(userDetails, req, bindingResult)
    }

    @Operation(summary = "쿠폰 발급 API", description = "쿠폰 등록 API 를 통해 등록된 쿠폰을 1회 발급 받을 수 있습니다.\n헤더에 Authorization 으로 JWT 토큰을 요구합니다.")
    @GetMapping("/issuance/{id}")
    fun issuanceCoupon(
        @Parameter(hidden = true) @AuthenticationPrincipal userDetails: UserDetailsImpl,
        @PathVariable id: Long
    ): DefaultResponseDto {
        return couponService.couponIssuance(userDetails, id)
    }
}