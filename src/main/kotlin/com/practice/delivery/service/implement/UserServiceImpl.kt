package com.practice.delivery.service.implement

import com.practice.delivery.dto.request.LoginRequestDto
import com.practice.delivery.dto.request.RegisterUserRequestDto
import com.practice.delivery.dto.response.LoginResponseDto
import com.practice.delivery.dto.response.ManageRegisterAdminResponseDto
import com.practice.delivery.dto.response.RegisterUserResponseDto
import com.practice.delivery.dto.response.ViewRegisterAdminRequestListResponseDto
import com.practice.delivery.entity.AdminUserRequest
import com.practice.delivery.entity.Role
import com.practice.delivery.entity.User
import com.practice.delivery.jwt.JwtTokenProvider
import com.practice.delivery.model.SimpleRegisterAdminRequest
import com.practice.delivery.repository.AdminUserRequestRepository
import com.practice.delivery.repository.UserRepository
import com.practice.delivery.service.UserService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.BindingResult
import java.time.LocalDateTime
import java.util.Objects
import javax.servlet.http.HttpServletResponse


@Service
class UserServiceImpl(
    private var passwordEncoder: BCryptPasswordEncoder,
    private var userRepository: UserRepository,
    private var jwtTokenProvider: JwtTokenProvider,
    private var adminUserRequestRepository: AdminUserRequestRepository
) : UserService {

    @Transactional
    override fun registerDefaultAndBusinessUser(
        req: RegisterUserRequestDto,
        bindingResult: BindingResult
    ): RegisterUserResponseDto {
        var res = RegisterUserResponseDto()
        if (bindingResult.hasErrors()) {
            res.code = HttpServletResponse.SC_BAD_REQUEST
            res.msg = bindingResult.allErrors[0].defaultMessage
        } else {
            if (userRepository.existsByEmail(req.email!!) or adminUserRequestRepository.existsByEmail(req.email)) {
                res.code = HttpServletResponse.SC_BAD_REQUEST
                res.msg = "이미 존재하는 이메일입니다."
            } else {
                var user = User()
                user.email = req.email
                user.password = passwordEncoder.encode(req.password)
                user.role = req.role!!
                userRepository.save(user)
                res.code = HttpServletResponse.SC_OK
                res.msg = "성공적으로 가입하였습니다."
                res.email = user.email

            }
        }
        return res
    }

    @Transactional
    override fun registerAdminUser(req: RegisterUserRequestDto, bindingResult: BindingResult): RegisterUserResponseDto {
        var res = RegisterUserResponseDto()
        if (bindingResult.hasErrors()) {
            res.code = HttpServletResponse.SC_BAD_REQUEST
            res.msg = bindingResult.allErrors[0].defaultMessage
        } else {
            if (req.role == Role.SUPERIOR_ADMIN) {
                res.code = HttpServletResponse.SC_BAD_REQUEST
                res.msg = "유효하지 않은 요청입니다."
            } else {
                if (userRepository.existsByEmail(req.email!!) or adminUserRequestRepository.existsByEmailAndStatus(req.email,AdminUserRequest.Status.AWAIT)) {
                    res.code = HttpServletResponse.SC_BAD_REQUEST
                    res.msg = "이미 존재하는 이메일입니다."
                } else {
                    var adminUserRequest = AdminUserRequest()
                    adminUserRequest.email = req.email
                    adminUserRequest.password = passwordEncoder.encode(req.password)
                    adminUserRequestRepository.save(adminUserRequest)
                    res.code = HttpServletResponse.SC_OK
                    res.msg = "성공적으로 신청하였습니다."
                    res.email = adminUserRequest.email
                }

            }
        }
        return res
    }

    @Transactional
    override fun login(req: LoginRequestDto, bindingResult: BindingResult): LoginResponseDto {
        var res = LoginResponseDto()
        if (bindingResult.hasErrors()) {
            res.code = HttpServletResponse.SC_BAD_REQUEST
            res.msg = bindingResult.allErrors[0].defaultMessage
        } else {
            var loadedUser = userRepository.findByEmail(req.email!!)
            if (Objects.isNull(loadedUser)) {
                res.code = HttpServletResponse.SC_BAD_REQUEST
                res.msg = "잘못된 이메일 또는 패스워드입니다."
            } else {
                if (!passwordEncoder.matches(req.password, loadedUser!!.password)) {
                    res.code = HttpServletResponse.SC_BAD_REQUEST
                    res.msg = "잘못된 이메일 또는 패스워드입니다."
                } else {
                    val token = jwtTokenProvider.createToken(loadedUser.email, loadedUser.id)
                    loadedUser.lastLoginDate = LocalDateTime.now()
                    res.code = HttpServletResponse.SC_OK
                    res.msg = "성공적으로 로그인 하였습니다."
                    res.token = token
                }
            }
        }
        return res
    }

    override fun viewRegisterAdminList(userDetails: UserDetailsImpl): ViewRegisterAdminRequestListResponseDto {
        var res = ViewRegisterAdminRequestListResponseDto()
        if (Objects.isNull(userDetails.getUser())) {
            res.code = HttpServletResponse.SC_FORBIDDEN
            res.msg = "권한이 부족합니다."
        } else {
            if ("SUPERIOR_ADMIN" !in userDetails.getUser().getAuthorities()){
                res.code = HttpServletResponse.SC_FORBIDDEN
                res.msg = "권한이 부족합니다."
            } else {
                var simpleRegisterAdminRequestList = arrayListOf<SimpleRegisterAdminRequest>()
                var adminUserRequestList = adminUserRequestRepository.findByStatus(AdminUserRequest.Status.AWAIT)
                for (adminUserRequest in adminUserRequestList) {
                    var simpleRegisterAdminRequest = SimpleRegisterAdminRequest(adminUserRequest)
                    simpleRegisterAdminRequestList.add(simpleRegisterAdminRequest)
                }
                res.simpleRequestList = simpleRegisterAdminRequestList
                res.msg = "성공적으로 불러왔습니다."
                res.code = HttpServletResponse.SC_OK
            }
        }
        return res
    }

    @Transactional
    override fun acceptRegisterAdmin(userDetails: UserDetailsImpl, id: Long): ManageRegisterAdminResponseDto {
        var res = ManageRegisterAdminResponseDto()
        if (Objects.isNull(userDetails.getUser())){
            res.code = HttpServletResponse.SC_FORBIDDEN
            res.msg = "권한이 부족합니다."
        } else {
            if ("SUPERIOR_ADMIN" !in userDetails.getUser().getAuthorities()){
                res.code = HttpServletResponse.SC_FORBIDDEN
                res.msg = "권한이 부족합니다."
            } else {
                var adminUserRequest = adminUserRequestRepository.findById(id)
                if (!adminUserRequestRepository.existsById(id)){
                    res.code = HttpServletResponse.SC_BAD_REQUEST
                    res.msg = "존재하지 않는 요청 ID입니다."
                } else {
                    adminUserRequest.get().acceptRequest(userDetails.getUser())
                    var adminUser = User()
                    adminUser.email = adminUserRequest.get().email
                    adminUser.password = adminUserRequest.get().password
                    adminUser.role = Role.ADMIN
                    userRepository.save(adminUser)
                    res.code = HttpServletResponse.SC_OK
                    res.msg = "성공적으로 수락하였습니다."
                    res.simpleRegisterAdminRequest = SimpleRegisterAdminRequest(adminUserRequest.get())
                }
            }
        }
        return res
    }

    @Transactional
    override fun denyRegisterAdmin(userDetails: UserDetailsImpl, id: Long): ManageRegisterAdminResponseDto {
        var res = ManageRegisterAdminResponseDto()
        if (Objects.isNull(userDetails.getUser())){
            res.code = HttpServletResponse.SC_FORBIDDEN
            res.msg = "권한이 부족합니다."
        } else {
            if ("SUPERIOR_ADMIN" !in userDetails.getUser().getAuthorities()){
                res.code = HttpServletResponse.SC_FORBIDDEN
                res.msg = "권한이 부족합니다."
            } else {
                var adminUserRequest = adminUserRequestRepository.findById(id)
                if (!adminUserRequestRepository.existsById(id)){
                    res.code = HttpServletResponse.SC_BAD_REQUEST
                    res.msg = "존재하지 않는 요청 ID입니다."
                } else {
                    adminUserRequest.get().denyRequest(userDetails.getUser())
                    res.code = HttpServletResponse.SC_OK
                    res.msg = "성공적으로 거절하였습니다."
                    res.simpleRegisterAdminRequest = SimpleRegisterAdminRequest(adminUserRequest.get())
                }
            }
        }
        return res
    }


}