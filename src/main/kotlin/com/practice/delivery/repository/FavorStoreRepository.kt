package com.practice.delivery.repository

import com.practice.delivery.entity.FavorStore
import com.practice.delivery.entity.Store
import com.practice.delivery.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface FavorStoreRepository:JpaRepository<FavorStore,Long> {

    fun existsByUserAndStore(user:User,store:Store):Boolean

}