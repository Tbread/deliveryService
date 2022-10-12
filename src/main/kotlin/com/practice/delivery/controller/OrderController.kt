package com.practice.delivery.controller

import com.practice.delivery.dto.request.OrderRequestDto
import com.practice.delivery.dto.request.UpdateOrderStatusRequestDto
import com.practice.delivery.dto.response.DefaultResponseDto
import com.practice.delivery.dto.response.ManageOrderResponseDto
import com.practice.delivery.dto.response.ViewOrderListResponseDto
import com.practice.delivery.service.OrderService
import com.practice.delivery.service.implemented.UserDetailsImpl
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@Tag(name = "주문 관련")
@RestController
@RequestMapping("/order")
class OrderController(private var orderService: OrderService) {

    @Operation(summary = "주문 API")
    @PostMapping("/create")
    fun order(
        @AuthenticationPrincipal userDetails: UserDetailsImpl,
        @RequestBody @Valid req: OrderRequestDto,
        bindingResult: BindingResult
    ): DefaultResponseDto {
        return orderService.order(userDetails, req, bindingResult)
    }

    @Operation(summary = "주문 조회 API", description = "일반 유저의 경우엔 자신의 주문 기록을 출력하며 사업자의 경우에는 자신의 가게에 할당된 주문을 조회합니다")
    @GetMapping("/view-list")
    fun viewOrderList(@AuthenticationPrincipal userDetails: UserDetailsImpl): ViewOrderListResponseDto {
        return orderService.viewOrderList(userDetails)
    }

    @Operation(summary = "주문 수락 API")
    @PostMapping("/accept/{id}")
    fun acceptOrder(
        @AuthenticationPrincipal userDetails: UserDetailsImpl,
        @PathVariable id: Long
    ): ManageOrderResponseDto {
        return orderService.acceptOrder(userDetails, id)
    }

    @Operation(summary = "주문 거절 API")
    @PostMapping("/deny/{id}")
    fun denyOrder(
        @AuthenticationPrincipal userDetails: UserDetailsImpl,
        @PathVariable id: Long
    ): ManageOrderResponseDto {
        return orderService.denyOrder(userDetails, id)
    }

    @Operation(summary = "주문 상태 업데이트 API", description = "주문의 상태를 업데이트합니다. 이미 취소된 주문건의 수정은 관리자만 가능합니다.")
    @PostMapping("/update-status/{id}")
    fun updateOrderProgress(
        @AuthenticationPrincipal userDetails: UserDetailsImpl,
        @PathVariable id: Long,@RequestBody req:UpdateOrderStatusRequestDto
    ): ManageOrderResponseDto {
        return orderService.updateOrderProgress(userDetails, id,req)
    }
}