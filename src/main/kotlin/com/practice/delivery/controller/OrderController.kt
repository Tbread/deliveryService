package com.practice.delivery.controller

import com.practice.delivery.dto.request.OrderRequestDto
import com.practice.delivery.dto.request.UpdateOrderStatusRequestDto
import com.practice.delivery.dto.response.DefaultResponseDto
import com.practice.delivery.dto.response.ManageOrderResponseDto
import com.practice.delivery.dto.response.ViewOrderListResponseDto
import com.practice.delivery.service.OrderService
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
@RequestMapping("/order")
class OrderController(private var orderService: OrderService) {

    @PostMapping("/create")
    fun order(
        @AuthenticationPrincipal userDetails: UserDetailsImpl,
        @RequestBody @Valid req: OrderRequestDto,
        bindingResult: BindingResult
    ): DefaultResponseDto {
        return orderService.order(userDetails, req, bindingResult)
    }

    @GetMapping("/view-list")
    fun viewOrderList(@AuthenticationPrincipal userDetails: UserDetailsImpl): ViewOrderListResponseDto {
        return orderService.viewOrderList(userDetails)
    }

    @PostMapping("/accept/{id}")
    fun acceptOrder(
        @AuthenticationPrincipal userDetails: UserDetailsImpl,
        @PathVariable id: Long
    ): ManageOrderResponseDto {
        return orderService.acceptOrder(userDetails, id)
    }

    @PostMapping("/deny/{id}")
    fun denyOrder(
        @AuthenticationPrincipal userDetails: UserDetailsImpl,
        @PathVariable id: Long
    ): ManageOrderResponseDto {
        return orderService.denyOrder(userDetails, id)
    }

    @PostMapping("/update-status/{id}")
    fun updateOrder(
        @AuthenticationPrincipal userDetails: UserDetailsImpl,
        @PathVariable id: Long,@RequestBody req:UpdateOrderStatusRequestDto
    ): ManageOrderResponseDto {
        return orderService.updateOrder(userDetails, id,req)
    }
}