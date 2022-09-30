package com.practice.delivery.repository

import com.practice.delivery.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository:JpaRepository<User,Long> {

    fun findByEmail(email:String):User?
    fun existsByEmail(email:String):Boolean

}