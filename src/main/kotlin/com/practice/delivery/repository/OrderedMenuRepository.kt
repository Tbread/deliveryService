package com.practice.delivery.repository

import com.practice.delivery.entity.DeliveryOrder
import com.practice.delivery.entity.OrderedMenu
import org.springframework.data.jpa.repository.JpaRepository

interface OrderedMenuRepository:JpaRepository<OrderedMenu,Long> {

    fun findByDeliveryOrder(deliveryOrder:DeliveryOrder):List<OrderedMenu>
}