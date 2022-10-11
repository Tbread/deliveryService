package com.practice.delivery.service

import com.practice.delivery.dto.request.AddMenuRequestDto
import com.practice.delivery.dto.request.UpdateMenuRequestDto
import com.practice.delivery.dto.response.AddMenuResponseDto
import com.practice.delivery.dto.response.DefaultResponseDto
import com.practice.delivery.dto.response.ShowMenuResponseDto
import com.practice.delivery.service.implemented.UserDetailsImpl
import org.springframework.validation.BindingResult

interface MenuService {

    fun addMenu(userDetails: UserDetailsImpl, req:AddMenuRequestDto, bindingResult: BindingResult):AddMenuResponseDto

    fun showMenuList(id:Long):ShowMenuResponseDto

    fun removeMenu(userDetails: UserDetailsImpl,id:Long):DefaultResponseDto

    fun updateMenu(userDetails: UserDetailsImpl, req:UpdateMenuRequestDto, id:Long,bindingResult: BindingResult):DefaultResponseDto
}