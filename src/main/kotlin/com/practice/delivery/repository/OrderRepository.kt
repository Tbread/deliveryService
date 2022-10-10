package com.practice.delivery.repository

import com.practice.delivery.entity.Order
import com.practice.delivery.entity.Store
import org.springframework.data.jpa.repository.JpaRepository

interface OrderRepository:JpaRepository<Order,Long> {

    fun findByStore(store:Store):List<Order>

    fun existsByStore(store: Store):Boolean
}