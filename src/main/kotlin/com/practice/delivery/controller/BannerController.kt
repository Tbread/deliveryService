package com.practice.delivery.controller

import com.practice.delivery.dto.request.AddBannerRequestDto
import com.practice.delivery.dto.response.AddBannerResponseDto
import com.practice.delivery.service.BannerService
import com.practice.delivery.service.implemented.UserDetailsImpl
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/banner")
class BannerController(private var bannerService: BannerService) {

    @PostMapping("/add")
    fun addBanner(
        @AuthenticationPrincipal userDetails: UserDetailsImpl,
        @RequestBody @Valid req: AddBannerRequestDto,
        bindingResult: BindingResult
    ): AddBannerResponseDto {
        return bannerService.addBanner(userDetails, req, bindingResult)
    }
}