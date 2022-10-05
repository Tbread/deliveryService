package com.practice.delivery.service.Implement

import com.practice.delivery.service.StoreService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StoreServiceImpl:StoreService {

    @Transactional
    override fun registerStore(userDetails: UserDetailsImpl, any: Any): Any {
        TODO("Not yet implemented")
    }

    override fun viewRegisterStoreRequestList(userDetails: UserDetailsImpl): Any {
        TODO("Not yet implemented")
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