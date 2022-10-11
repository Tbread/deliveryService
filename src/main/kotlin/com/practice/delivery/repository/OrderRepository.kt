package com.practice.delivery.repository

import com.practice.delivery.entity.DeliveryOrder
import com.practice.delivery.entity.Store
import com.practice.delivery.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface OrderRepository:JpaRepository<DeliveryOrder,Long> {

    fun findByStore(store:Store):List<DeliveryOrder>

    fun existsByStore(store: Store):Boolean

    fun findByOrderer(orderer:User):List<DeliveryOrder>
}