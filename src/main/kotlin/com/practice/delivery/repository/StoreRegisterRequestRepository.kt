package com.practice.delivery.repository

import com.practice.delivery.entity.StoreRegisterRequest
import com.practice.delivery.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface StoreRegisterRequestRepository:JpaRepository<StoreRegisterRequest,Long> {
    fun findByOwner(owner:User):StoreRegisterRequest?
    fun existsByOwnerAndStatus(owner: User,status:StoreRegisterRequest.Status):Boolean
}