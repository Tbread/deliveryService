package com.practice.delivery.repository

import com.practice.delivery.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface userRepository:JpaRepository<User,Long> {

    fun findByUsername(username:String):User?

}