package com.practice.delivery.service

import com.practice.delivery.dto.request.OrderRequestDto
import com.practice.delivery.dto.response.DefaultResponseDto
import com.practice.delivery.service.implemented.UserDetailsImpl

interface OrderService {

    fun order(userDetails: UserDetailsImpl,req:OrderRequestDto):DefaultResponseDto
}