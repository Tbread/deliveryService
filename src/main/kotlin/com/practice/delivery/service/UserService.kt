package com.practice.delivery.service

import com.practice.delivery.dto.request.RegisterUserRequestDto
import com.practice.delivery.dto.response.RegisterUserResponseDto
import com.practice.delivery.entity.Role
import com.practice.delivery.entity.User
import com.practice.delivery.jwt.JwtTokenProvider
import com.practice.delivery.repository.AdminUserRequestRepository
import com.practice.delivery.repository.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.BindingResult
import javax.servlet.http.HttpServletResponse


@Service
class UserService(
    private var passwordEncoder: BCryptPasswordEncoder,
    private var userRepository: UserRepository,
    private var jwtTokenProvider: JwtTokenProvider,
    private var adminUserRequestRepository: AdminUserRequestRepository
) {

    @Transactional
    fun registerDefaultUser(req: RegisterUserRequestDto, bindingResult: BindingResult): RegisterUserResponseDto {
        var res = RegisterUserResponseDto()
        if (bindingResult.hasErrors()) {
            res.code = HttpServletResponse.SC_BAD_REQUEST
            res.msg = bindingResult.allErrors[0].toString()
        } else {
            if (userRepository.existsByEmail(req.email!!) or adminUserRequestRepository.existsByEmail(req.email)) {
                res.code = HttpServletResponse.SC_BAD_REQUEST
                res.msg = "이미 존재하는 이메일입니다."
            } else {
                var user = User()
                user.email = req.email
                user.password = passwordEncoder.encode(req.password)
                user.role = Role.ROLE_DEFAULT
                userRepository.save(user)
                res.code = HttpServletResponse.SC_OK
                res.msg = "성공적으로 가입하였습니다."
                res.email = user.email

            }
        }
        return res
    }
}