package com.practice.delivery.service.Implement

import com.practice.delivery.dto.request.RegisterStoreRequestDto
import com.practice.delivery.dto.response.RegisterStoreResponseDto
import com.practice.delivery.dto.response.ViewRegisterStoreRequestListResponseDto
import com.practice.delivery.entity.StoreRegisterRequest
import com.practice.delivery.model.SimpleRegisterStoreRequest
import com.practice.delivery.repository.StoreRegisterRequestRepository
import com.practice.delivery.repository.StoreRepository
import com.practice.delivery.service.StoreService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.BindingResult
import java.util.Objects
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
                    res.code = HttpServletResponse.SC_FORBIDDEN
                    res.msg = "이미 등록되거나 신청 대기 상태입니다"
                } else {
                    if (bindingResult.hasErrors()) {
                        res.code = HttpServletResponse.SC_BAD_REQUEST
                        res.msg = bindingResult.allErrors[0].defaultMessage
                    } else {
                        var registerRequest = StoreRegisterRequest()
                        registerRequest.owner = userDetails.getUser()
                        registerRequest.storeName = req.storeName
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
        if (Objects.isNull(userDetails.getUser())){
            res.code = HttpServletResponse.SC_FORBIDDEN
            res.msg = "권한이 부족합니다."
        } else {
            if("ADMIN" !in userDetails.getUser().getAuthorities()){
                res.code = HttpServletResponse.SC_FORBIDDEN
                res.msg = "권한이 부족합니다."
            } else {
                var storeRegisterRequestList = storeRegisterRequestRepository.findByStatus(StoreRegisterRequest.Status.AWAIT)
                var simpleRequestList = arrayListOf<SimpleRegisterStoreRequest>()
                for (request in storeRegisterRequestList){
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
    override fun acceptRegisterStoreRequest(userDetails: UserDetailsImpl, id: Long): Any {
        TODO("Not yet implemented")
    }

    @Transactional
    override fun denyRegisterStoreRequest(userDetails: UserDetailsImpl, id: Long): Any {
        TODO("Not yet implemented")
    }
}