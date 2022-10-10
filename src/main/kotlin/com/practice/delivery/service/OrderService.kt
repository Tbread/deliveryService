package com.practice.delivery.service

import com.practice.delivery.dto.request.OrderRequestDto
import com.practice.delivery.dto.response.DefaultResponseDto
import com.practice.delivery.entity.Order
import com.practice.delivery.service.implemented.UserDetailsImpl

interface OrderService {

    fun order(userDetails: UserDetailsImpl,req:OrderRequestDto):DefaultResponseDto

    fun viewOrderList(userDetails: UserDetailsImpl):Any

    fun acceptOrder(userDetails: UserDetailsImpl,id:Long):Any

    fun denyOrder(userDetails: UserDetailsImpl,id:Long):Any

    fun updateOrder(userDetails: UserDetailsImpl,id: Long,status:Order.Status):Any

}