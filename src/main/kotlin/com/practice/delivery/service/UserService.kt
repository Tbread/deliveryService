package com.practice.delivery.service

import com.practice.delivery.dto.request.LoginRequestDto
import com.practice.delivery.dto.request.RegisterUserRequestDto
import com.practice.delivery.dto.response.LoginResponseDto
import com.practice.delivery.dto.response.RegisterUserResponseDto
import com.practice.delivery.dto.response.ViewRegisterAdminRequestListResponseDto
import com.practice.delivery.service.Implement.UserDetailsImpl
import org.springframework.stereotype.Service
import org.springframework.validation.BindingResult
import javax.servlet.http.HttpServletResponse

interface UserService {

    fun registerDefaultAndBusinessUser(req: RegisterUserRequestDto, bindingResult: BindingResult): RegisterUserResponseDto

    fun registerAdminUser(req: RegisterUserRequestDto, bindingResult: BindingResult): RegisterUserResponseDto

    fun login(req:LoginRequestDto,bindingResult: BindingResult):LoginResponseDto

    fun viewRegisterAdminList(userDetails:UserDetailsImpl):ViewRegisterAdminRequestListResponseDto

}