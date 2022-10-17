package com.practice.delivery.controller

import com.practice.delivery.dto.request.AddBannerRequestDto
import com.practice.delivery.dto.response.AddBannerResponseDto
import com.practice.delivery.dto.response.DefaultResponseDto
import com.practice.delivery.dto.response.ViewBannerListResponseDto
import com.practice.delivery.service.BannerService
import com.practice.delivery.service.implemented.UserDetailsImpl
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@Tag(name = "배너 관련")
@RestController
@RequestMapping("/banner")
class BannerController(private var bannerService: BannerService) {

    @Operation(
        summary = "배너 등록 API",
        description = "새로운 배너를 정의하여 등록합니다.<br />헤더에 Authorization 으로 JWT 토큰을 요구합니다."
    )
    @PostMapping("/add")
    fun addBanner(
        @AuthenticationPrincipal userDetails: UserDetailsImpl,
        @RequestBody @Valid req: AddBannerRequestDto,
        bindingResult: BindingResult
    ): AddBannerResponseDto {
        return bannerService.addBanner(userDetails, req, bindingResult)
    }

    @Operation(
        summary = "배너 만료 API",
        description = "배너를 만료처리합니다. 해당 배너는 만료일자까지 배너 조회 API 를 통해 조회할 수 있습니다.<br />헤더에 Authorization 으로 JWT 토큰을 요구합니다."
    )
    @DeleteMapping("/expire")
    fun expireBanner(
        @AuthenticationPrincipal userDetails: UserDetailsImpl,
        @PathVariable id: Long
    ): DefaultResponseDto {
        return bannerService.expireBanner(userDetails, id)
    }

    @Operation(
        summary = "배너 조회 API"
    )
    @GetMapping("/view")
    fun viewBannerList(): ViewBannerListResponseDto {
        return bannerService.viewBannerList()
    }
}