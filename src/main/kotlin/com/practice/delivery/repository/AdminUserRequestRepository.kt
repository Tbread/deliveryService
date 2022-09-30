package com.practice.delivery.repository

import com.practice.delivery.entity.AdminUserRequest
import org.springframework.data.jpa.repository.JpaRepository

interface AdminUserRequestRepository:JpaRepository<AdminUserRequest,Long> {

    fun existsByEmail(email:String):Boolean

}