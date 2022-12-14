package com.practice.delivery.service.implemented

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
import com.practice.delivery.repository.dslRepository.QAdminUserRequestRepository
import com.practice.delivery.service.UserService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.BindingResult
import java.util.*
import javax.servlet.http.HttpServletResponse


@Service
class UserServiceImpl(
    private var passwordEncoder: BCryptPasswordEncoder,
    private var userRepository: UserRepository,
    private var jwtTokenProvider: JwtTokenProvider,
    private var adminUserRequestRepository: AdminUserRequestRepository,
    private var qAdminUserRequestRepository: QAdminUserRequestRepository
) : UserService {

    @Transactional
    override fun registerDefaultAndBusinessUser(
        req: RegisterUserRequestDto,
        bindingResult: BindingResult
    ): RegisterUserResponseDto {
        val res = RegisterUserResponseDto()
        if (bindingResult.hasErrors()) {
            res.code = HttpServletResponse.SC_BAD_REQUEST
            res.msg = bindingResult.allErrors[0].defaultMessage
        } else {
            if (userRepository.existsByEmail(req.email!!) or adminUserRequestRepository.existsByEmail(req.email)) {
                res.code = HttpServletResponse.SC_BAD_REQUEST
                res.msg = "?????? ???????????? ??????????????????."
            } else {
                val user = User()
                user.email = req.email
                user.password = passwordEncoder.encode(req.password)
                user.role = req.role!!
                userRepository.save(user)
                res.code = HttpServletResponse.SC_OK
                res.msg = "??????????????? ?????????????????????."
                res.email = user.email

            }
        }
        return res
    }

    @Transactional
    override fun registerAdminUser(req: RegisterUserRequestDto, bindingResult: BindingResult): RegisterUserResponseDto {
        val res = RegisterUserResponseDto()
        if (bindingResult.hasErrors()) {
            res.code = HttpServletResponse.SC_BAD_REQUEST
            res.msg = bindingResult.allErrors[0].defaultMessage
        } else {
            if (req.role == Role.SUPERIOR_ADMIN) {
                res.code = HttpServletResponse.SC_BAD_REQUEST
                res.msg = "???????????? ?????? ???????????????."
            } else {
                if (userRepository.existsByEmail(req.email!!) or adminUserRequestRepository.existsByEmailAndStatus(
                        req.email,
                        AdminUserRequest.Status.AWAIT
                    )
                ) {
                    res.code = HttpServletResponse.SC_BAD_REQUEST
                    res.msg = "?????? ???????????? ??????????????????."
                } else {
                    val adminUserRequest = AdminUserRequest()
                    adminUserRequest.email = req.email
                    adminUserRequest.password = passwordEncoder.encode(req.password)
                    adminUserRequestRepository.save(adminUserRequest)
                    res.code = HttpServletResponse.SC_OK
                    res.msg = "??????????????? ?????????????????????."
                    res.email = adminUserRequest.email
                }

            }
        }
        return res
    }

    @Transactional
    override fun login(req: LoginRequestDto, bindingResult: BindingResult): LoginResponseDto {
        val res = LoginResponseDto()
        if (bindingResult.hasErrors()) {
            res.code = HttpServletResponse.SC_BAD_REQUEST
            res.msg = bindingResult.allErrors[0].defaultMessage
        } else {
            val loadedUser = userRepository.findByEmail(req.email!!)
            if (Objects.isNull(loadedUser)) {
                res.code = HttpServletResponse.SC_BAD_REQUEST
                res.msg = "????????? ????????? ?????? ?????????????????????."
            } else {
                if (!passwordEncoder.matches(req.password, loadedUser!!.password)) {
                    res.code = HttpServletResponse.SC_BAD_REQUEST
                    res.msg = "????????? ????????? ?????? ?????????????????????."
                } else {
                    val token = jwtTokenProvider.createToken(loadedUser.email, loadedUser.id)
                    loadedUser.updateLoginDate()
                    res.code = HttpServletResponse.SC_OK
                    res.msg = "??????????????? ????????? ???????????????."
                    res.token = token
                }
            }
        }
        return res
    }

    override fun viewRegisterAdminList(
        userDetails: UserDetailsImpl,
        statusCode: Int?
    ): ViewRegisterAdminRequestListResponseDto {
        val res = ViewRegisterAdminRequestListResponseDto()
        if (Objects.isNull(userDetails.getUser())) {
            res.code = HttpServletResponse.SC_FORBIDDEN
            res.msg = "????????? ???????????????."
        } else {
            if ("SUPERIOR_ADMIN" !in userDetails.getUser().getAuthorities()) {
                res.code = HttpServletResponse.SC_FORBIDDEN
                res.msg = "????????? ???????????????."
            } else {
                val simpleRegisterAdminRequestList = arrayListOf<SimpleRegisterAdminRequest>()
                val adminUserRequestList:List<AdminUserRequest> = when (statusCode) {
                    0 -> {
                        qAdminUserRequestRepository.findAllByStatus(AdminUserRequest.Status.AWAIT)
                    }
                    1 -> {
                        qAdminUserRequestRepository.findAllByStatus(AdminUserRequest.Status.ACCEPTED)
                    }
                    2 -> {
                        qAdminUserRequestRepository.findAllByStatus(AdminUserRequest.Status.DENIED)
                    }
                    else -> {
                        qAdminUserRequestRepository.findAll()
                    }
                }
                for (adminUserRequest in adminUserRequestList) {
                    val simpleRegisterAdminRequest = SimpleRegisterAdminRequest(adminUserRequest)
                    simpleRegisterAdminRequestList.add(simpleRegisterAdminRequest)
                }
                res.simpleRequestList = simpleRegisterAdminRequestList
                res.msg = "??????????????? ??????????????????."
                res.code = HttpServletResponse.SC_OK
            }
        }
        return res
    }

    @Transactional
    override fun acceptRegisterAdmin(userDetails: UserDetailsImpl, id: Long): ManageRegisterAdminResponseDto {
        val res = ManageRegisterAdminResponseDto()
        if (Objects.isNull(userDetails.getUser())) {
            res.code = HttpServletResponse.SC_FORBIDDEN
            res.msg = "????????? ???????????????."
        } else {
            if ("SUPERIOR_ADMIN" !in userDetails.getUser().getAuthorities()) {
                res.code = HttpServletResponse.SC_FORBIDDEN
                res.msg = "????????? ???????????????."
            } else {
                val adminUserRequest = adminUserRequestRepository.findById(id)
                if (!adminUserRequestRepository.existsById(id)) {
                    res.code = HttpServletResponse.SC_BAD_REQUEST
                    res.msg = "???????????? ?????? ?????? ID?????????."
                } else {
                    adminUserRequest.get().acceptRequest(userDetails.getUser())
                    val adminUser = User()
                    adminUser.email = adminUserRequest.get().email
                    adminUser.password = adminUserRequest.get().password
                    adminUser.role = Role.ADMIN
                    userRepository.save(adminUser)
                    res.code = HttpServletResponse.SC_OK
                    res.msg = "??????????????? ?????????????????????."
                    res.simpleRegisterAdminRequest = SimpleRegisterAdminRequest(adminUserRequest.get())
                }
            }
        }
        return res
    }

    @Transactional
    override fun denyRegisterAdmin(userDetails: UserDetailsImpl, id: Long): ManageRegisterAdminResponseDto {
        val res = ManageRegisterAdminResponseDto()
        if (Objects.isNull(userDetails.getUser())) {
            res.code = HttpServletResponse.SC_FORBIDDEN
            res.msg = "????????? ???????????????."
        } else {
            if ("SUPERIOR_ADMIN" !in userDetails.getUser().getAuthorities()) {
                res.code = HttpServletResponse.SC_FORBIDDEN
                res.msg = "????????? ???????????????."
            } else {
                val adminUserRequest = adminUserRequestRepository.findById(id)
                if (!adminUserRequestRepository.existsById(id)) {
                    res.code = HttpServletResponse.SC_BAD_REQUEST
                    res.msg = "???????????? ?????? ?????? ID?????????."
                } else {
                    adminUserRequest.get().denyRequest(userDetails.getUser())
                    res.code = HttpServletResponse.SC_OK
                    res.msg = "??????????????? ?????????????????????."
                    res.simpleRegisterAdminRequest = SimpleRegisterAdminRequest(adminUserRequest.get())
                }
            }
        }
        return res
    }


}