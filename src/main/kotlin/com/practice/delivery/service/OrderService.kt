package com.practice.delivery.service

import com.practice.delivery.dto.request.OrderRequestDto
import com.practice.delivery.dto.request.UpdateOrderStatusRequestDto
import com.practice.delivery.dto.response.DefaultResponseDto
import com.practice.delivery.dto.response.ManageOrderResponseDto
import com.practice.delivery.dto.response.ViewOrderListResponseDto
import com.practice.delivery.entity.Order
import com.practice.delivery.service.implemented.UserDetailsImpl
import org.springframework.validation.BindingResult

interface OrderService {

    fun order(userDetails: UserDetailsImpl,req:OrderRequestDto,bindingResult: BindingResult):DefaultResponseDto

    fun viewOrderList(userDetails: UserDetailsImpl): ViewOrderListResponseDto

    fun acceptOrder(userDetails: UserDetailsImpl,id:Long): ManageOrderResponseDto

    fun denyOrder(userDetails: UserDetailsImpl,id:Long):ManageOrderResponseDto

    fun updateOrder(userDetails: UserDetailsImpl,id: Long,req:UpdateOrderStatusRequestDto):ManageOrderResponseDto

}