package com.practice.delivery.repository

import com.practice.delivery.entity.Store
import com.practice.delivery.entity.User
import com.sun.org.apache.xpath.internal.operations.Bool
import org.springframework.data.jpa.repository.JpaRepository

interface StoreRepository:JpaRepository<Store,Long> {

    fun findByOwner(owner:User):Store?
    fun existsByOwner(owner:User):Boolean
}