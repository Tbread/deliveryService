package com.practice.delivery.repository

import com.practice.delivery.entity.DeliveryOrder
import com.practice.delivery.entity.Store
import org.springframework.data.jpa.repository.JpaRepository

interface OrderRepository : JpaRepository<DeliveryOrder, Long> {

    fun existsByStore(store: Store): Boolean

}