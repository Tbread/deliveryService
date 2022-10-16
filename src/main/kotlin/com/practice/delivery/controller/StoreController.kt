package com.practice.delivery.controller

import com.practice.delivery.dto.request.AddMenuRequestDto
import com.practice.delivery.dto.request.RegisterStoreRequestDto
import com.practice.delivery.dto.request.UpdateMenuRequestDto
import com.practice.delivery.dto.request.UpdateStoreRequestDto
import com.practice.delivery.dto.response.*
import com.practice.delivery.service.MenuService
import com.practice.delivery.service.StoreService
import com.practice.delivery.service.implemented.UserDetailsImpl
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import javax.validation.Valid


@Tag(name = "가게 관리")
@RestController
@RequestMapping("/store")
class StoreController(private var storeService: StoreService, private var menuService: MenuService) {

    @Operation(summary = "가게 등록 신청 API", description = "헤더에 Authorization 으로 JWT 토큰을 요구합니다.")
    @PostMapping("/register")
    fun registerStore(
        @Parameter(hidden = true) @AuthenticationPrincipal userDetails: UserDetailsImpl,
        @Valid @RequestBody req: RegisterStoreRequestDto,
        bindingResult: BindingResult
    ): RegisterStoreResponseDto {
        return storeService.registerStore(userDetails, req, bindingResult)
    }

    @Operation(
        summary = "가게 등록 신청 리스트 조회 API",
        description = "statusCode: 0=대기중,1=수락됨,2=거절됨을 뜻합니다.<br />입력하지 않거나 잘못된 입력은 모든 정보를 가져옵니다.<br /> 헤더에 Authorization 으로 JWT 토큰을 요구합니다."
    )
    @GetMapping("/register-store-request-list")
    fun viewRegisterStoreList(
        @Parameter(hidden = true) @AuthenticationPrincipal userDetails: UserDetailsImpl,
        @RequestParam(required = false, value = "statusCode") statusCode: Int?
    ): ViewRegisterStoreRequestListResponseDto {
        return storeService.viewRegisterStoreRequestList(userDetails, statusCode)
    }


    @Operation(summary = "가게 등록 신청 수락 API", description = "헤더에 Authorization 으로 JWT 토큰을 요구합니다.")
    @GetMapping("/accept-register-request/{id}")
    fun acceptRegisterStore(
        @Parameter(hidden = true) @AuthenticationPrincipal userDetails: UserDetailsImpl,
        @PathVariable id: Long
    ): ManageRegisterStoreResponseDto {
        return storeService.acceptRegisterStoreRequest(userDetails, id)
    }

    @Operation(summary = "가게 등록 신청 거절 API", description = "헤더에 Authorization 으로 JWT 토큰을 요구합니다.")
    @GetMapping("/deny-register-request/{id}")
    fun denyRegisterStore(
        @Parameter(hidden = true) @AuthenticationPrincipal userDetails: UserDetailsImpl,
        @PathVariable id: Long
    ): ManageRegisterStoreResponseDto {
        return storeService.denyRegisterStoreRequest(userDetails, id)
    }

    @Operation(summary = "가게 메뉴 등록 API", description = "헤더에 Authorization 으로 JWT 토큰을 요구합니다.")
    @PostMapping("/add-menu")
    fun addMenu(
        @Parameter(hidden = true) @AuthenticationPrincipal userDetails: UserDetailsImpl,
        @RequestBody @Valid req: AddMenuRequestDto,
        bindingResult: BindingResult
    ): AddMenuResponseDto {
        return menuService.addMenu(userDetails, req, bindingResult)
    }

    @Operation(summary = "가게 메뉴 리스트 조회 API")
    @GetMapping("/show-menu-list/{id}")
    fun showMenuList(@PathVariable id: Long): ShowMenuResponseDto {
        return menuService.showMenuList(id)
    }

    @Operation(summary = "가게 메뉴 제거 API", description = "헤더에 Authorization 으로 JWT 토큰을 요구합니다.")
    @DeleteMapping("/remove-menu/{id}")
    fun removeMenu(
        @Parameter(hidden = true) @AuthenticationPrincipal userDetails: UserDetailsImpl,
        @PathVariable id: Long
    ): DefaultResponseDto {
        return menuService.removeMenu(userDetails, id)
    }

    @Operation(summary = "가게 메뉴 수정 API", description = "헤더에 Authorization 으로 JWT 토큰을 요구합니다.")
    @PatchMapping("/update-menu/{id}")
    fun updateMenu(
        @Parameter(hidden = true) @AuthenticationPrincipal userDetails: UserDetailsImpl,
        @RequestBody @Valid req: UpdateMenuRequestDto,
        @PathVariable id: Long,
        bindingResult: BindingResult
    ): DefaultResponseDto {
        return menuService.updateMenu(userDetails, req, id, bindingResult)
    }

    @Operation(summary = "가게 정보 업데이트 API", description = "헤더에 Authorization 으로 JWT 토큰을 요구합니다.")
    @PatchMapping("/update-info")
    fun updateStoreInfo(
        @Parameter(hidden = true) @AuthenticationPrincipal userDetails: UserDetailsImpl,
        @RequestBody @Valid req: UpdateStoreRequestDto,
        bindingResult: BindingResult
    ): DefaultResponseDto {
        return storeService.updateStoreInfo(userDetails, req, bindingResult)
    }

    @Operation(summary = "가게 찜 목록 추가/삭제 API", description = "헤더에 Authorization 으로 JWT 토큰을 요구합니다.")
    @GetMapping("/manage-favor/{id}")
    fun addFavorStore(
        @Parameter(hidden = true) @AuthenticationPrincipal userDetails: UserDetailsImpl,
        @PathVariable id: Long
    ): DefaultResponseDto {
        return storeService.manageFavorStore(userDetails, id)
    }

    @Operation(summary = "가게 찜 목록 조회 API", description = "헤더에 Authorization 으로 JWT 토큰을 요구합니다.")
    @GetMapping("/view-favor")
    fun viewFavorStoreList(
        @Parameter(hidden = true) @AuthenticationPrincipal userDetails: UserDetailsImpl
    ): ViewFavorStoreResponseDto {
        return storeService.viewFavorStoreList(userDetails)
    }
}