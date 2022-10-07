package com.practice.delivery.controller

import com.practice.delivery.dto.request.AddMenuRequestDto
import com.practice.delivery.dto.request.RegisterStoreRequestDto
import com.practice.delivery.dto.response.AddMenuResponseDto
import com.practice.delivery.dto.response.ManageRegisterStoreResponseDto
import com.practice.delivery.dto.response.RegisterStoreResponseDto
import com.practice.delivery.dto.response.ViewRegisterStoreRequestListResponseDto

import com.practice.delivery.service.implemented.UserDetailsImpl
import com.practice.delivery.service.MenuService
import com.practice.delivery.service.StoreService
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
@RequestMapping("/store")
class StoreController(private var storeService: StoreService, private var menuService: MenuService) {

    @PostMapping("/register")
    fun registerStore(
        @AuthenticationPrincipal userDetails: UserDetailsImpl,
        @Valid @RequestBody req: RegisterStoreRequestDto,
        bindingResult: BindingResult
    ): RegisterStoreResponseDto {
        return storeService.registerStore(userDetails, req, bindingResult)
    }

    @GetMapping("/register-store-request-list")
    fun viewRegisterStoreList(@AuthenticationPrincipal userDetails: UserDetailsImpl): ViewRegisterStoreRequestListResponseDto {
        return storeService.viewRegisterStoreRequestList(userDetails)
    }


    @GetMapping("/accept-register-request/{id}")
    fun acceptRegisterStore(
        @AuthenticationPrincipal userDetails: UserDetailsImpl,
        @PathVariable id: Long
    ): ManageRegisterStoreResponseDto {
        return storeService.acceptRegisterStoreRequest(userDetails, id)
    }

    @GetMapping("/deny-register-request/{id}")
    fun denyRegisterStore(
        @AuthenticationPrincipal userDetails: UserDetailsImpl,
        @PathVariable id: Long
    ): ManageRegisterStoreResponseDto {
        return storeService.denyRegisterStoreRequest(userDetails, id)
    }

    @PostMapping("/add-menu")
    fun addMenu(
        @AuthenticationPrincipal userDetails: UserDetailsImpl,
        @RequestBody @Valid req: AddMenuRequestDto,
        bindingResult: BindingResult
    ): AddMenuResponseDto {
        return menuService.addMenu(userDetails, req, bindingResult)
    }
}