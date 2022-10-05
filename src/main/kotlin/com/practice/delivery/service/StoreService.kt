package com.practice.delivery.service

import com.practice.delivery.service.Implement.UserDetailsImpl
import org.springframework.security.core.annotation.AuthenticationPrincipal

interface StoreService {

    fun registerStore(@AuthenticationPrincipal userDetails: UserDetailsImpl,any: Any):Any

    fun viewRegisterStoreRequestList(@AuthenticationPrincipal userDetails: UserDetailsImpl):Any

    fun acceptRegisterStoreRequest(@AuthenticationPrincipal userDetails: UserDetailsImpl,id:Long):Any

    fun denyRegisterStoreRequest(@AuthenticationPrincipal userDetails: UserDetailsImpl,id:Long):Any
}