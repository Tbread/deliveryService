package com.practice.delivery.service.implemented

import com.practice.delivery.dto.request.RegisterStoreRequestDto
import com.practice.delivery.dto.response.ManageRegisterStoreResponseDto
import com.practice.delivery.dto.response.RegisterStoreResponseDto
import com.practice.delivery.dto.response.ViewRegisterStoreRequestListResponseDto
import com.practice.delivery.entity.Store
import com.practice.delivery.entity.StoreRegisterRequest
import com.practice.delivery.model.SimpleRegisterStoreRequest
import com.practice.delivery.repository.StoreRegisterRequestRepository
import com.practice.delivery.repository.StoreRepository
import com.practice.delivery.service.StoreService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.BindingResult
import java.util.*
import javax.servlet.http.HttpServletResponse

@Service
class StoreServiceImpl(
    private var storeRegisterRequestRepository: StoreRegisterRequestRepository,
    private var storeRepository: StoreRepository
) : StoreService {

    @Transactional
    override fun registerStore(
        userDetails: UserDetailsImpl,
        req: RegisterStoreRequestDto,
        bindingResult: BindingResult
    ): RegisterStoreResponseDto {
        var res = RegisterStoreResponseDto()
        if (Objects.isNull(userDetails.getUser())) {
            res.code = HttpServletResponse.SC_FORBIDDEN
            res.msg = "권한이 부족합니다."
        } else {
            if ("BUSINESS" !in userDetails.getUser().getAuthorities()) {
                res.code = HttpServletResponse.SC_FORBIDDEN
                res.msg = "권한이 부족합니다."
            } else {
                if (storeRepository.existsByOwner(userDetails.getUser()) or storeRegisterRequestRepository.existsByOwnerAndStatus(
                        userDetails.getUser(),
                        StoreRegisterRequest.Status.AWAIT
                    )
                ) {
                    res.code = HttpServletResponse.SC_BAD_REQUEST
                    res.msg = "이미 등록되었거나 신청 대기 상태입니다"
                } else {
                    if (bindingResult.hasErrors()) {
                        res.code = HttpServletResponse.SC_BAD_REQUEST
                        res.msg = bindingResult.allErrors[0].defaultMessage
                    } else {
                        var registerRequest = StoreRegisterRequest()
                        registerRequest.owner = userDetails.getUser()
                        registerRequest.storeName = req.storeName!!
                        registerRequest.storeDesc = req.storeDesc
                        registerRequest.storeImgSrc = req.storeImgSrc
                        if (Objects.isNull(req.minOrderPrice)) {
                            registerRequest.minOrderPrice = 0
                        } else {
                            registerRequest.minOrderPrice = req.minOrderPrice!!
                        }
                        registerRequest.status = StoreRegisterRequest.Status.AWAIT
                        storeRegisterRequestRepository.save(registerRequest)
                        res.code = HttpServletResponse.SC_OK
                        res.msg = "성공적으로 신청하였습니다."
                        res.storeName = registerRequest.storeName
                    }
                }
            }
        }
        return res
    }

    override fun viewRegisterStoreRequestList(userDetails: UserDetailsImpl): ViewRegisterStoreRequestListResponseDto {
        var res = ViewRegisterStoreRequestListResponseDto()
        if (Objects.isNull(userDetails.getUser())) {
            res.code = HttpServletResponse.SC_FORBIDDEN
            res.msg = "권한이 부족합니다."
        } else {
            if ("ADMIN" !in userDetails.getUser().getAuthorities()) {
                res.code = HttpServletResponse.SC_FORBIDDEN
                res.msg = "권한이 부족합니다."
            } else {
                var storeRegisterRequestList =
                    storeRegisterRequestRepository.findByStatus(StoreRegisterRequest.Status.AWAIT)
                var simpleRequestList = arrayListOf<SimpleRegisterStoreRequest>()
                for (request in storeRegisterRequestList) {
                    var simpleRequest = SimpleRegisterStoreRequest(request)
                    simpleRequestList.add(simpleRequest)
                }
                res.code = HttpServletResponse.SC_OK
                res.msg = "성공적으로 불러왔습니다."
                res.simpleRequestList = simpleRequestList
            }
        }
        return res
    }

    @Transactional
    override fun acceptRegisterStoreRequest(userDetails: UserDetailsImpl, id: Long): ManageRegisterStoreResponseDto {
        var res = ManageRegisterStoreResponseDto()
        if (Objects.isNull(userDetails.getUser())) {
            res.code = HttpServletResponse.SC_FORBIDDEN
            res.msg = "권한이 부족합니다."
        } else {
            if ("ADMIN" !in userDetails.getUser().getAuthorities()) {
                res.code = HttpServletResponse.SC_FORBIDDEN
                res.msg = "권한이 부족합니다."
            } else {
                var storeRequest = storeRegisterRequestRepository.findById(id)
                if (!storeRegisterRequestRepository.existsById(id)) {
                    res.code = HttpServletResponse.SC_BAD_REQUEST
                    res.msg = "존재하지 않는 요청 ID입니다."
                } else {
                    var store = Store()
                    store.storeName = storeRequest.get().storeName
                    store.storeDesc = storeRequest.get().storeDesc
                    store.storeImgSrc = storeRequest.get().storeImgSrc
                    store.owner = storeRequest.get().owner
                    if (Objects.isNull(storeRequest.get().minOrderPrice)) {
                        store.minOrderPrice = 0
                    } else {
                        store.minOrderPrice = storeRequest.get().minOrderPrice
                    }
                    storeRepository.save(store)
                    storeRequest.get().acceptRequest(userDetails.getUser())
                    res.code = HttpServletResponse.SC_OK
                    res.msg = "성공적으로 수락하였습니다."
                    res.simpleRegisterStoreRequest = SimpleRegisterStoreRequest(storeRequest.get())
                }
            }
        }
        return res
    }

    @Transactional
    override fun denyRegisterStoreRequest(userDetails: UserDetailsImpl, id: Long): ManageRegisterStoreResponseDto {
        var res = ManageRegisterStoreResponseDto()
        if (Objects.isNull(userDetails.getUser())) {
            res.code = HttpServletResponse.SC_FORBIDDEN
            res.msg = "권한이 부족합니다."
        } else {
            if ("ADMIN" !in userDetails.getUser().getAuthorities()) {
                res.code = HttpServletResponse.SC_FORBIDDEN
                res.msg = "권한이 부족합니다."
            } else {
                var storeRequest = storeRegisterRequestRepository.findById(id)
                if (!storeRegisterRequestRepository.existsById(id)) {
                    res.code = HttpServletResponse.SC_BAD_REQUEST
                    res.msg = "존재하지 않는 요청 ID입니다."
                } else {
                    storeRequest.get().denyRequest(userDetails.getUser())
                    res.code = HttpServletResponse.SC_OK
                    res.msg = "성공적으로 거절하였습니다."
                    res.simpleRegisterStoreRequest = SimpleRegisterStoreRequest(storeRequest.get())
                }
            }
        }
        return res
    }

}