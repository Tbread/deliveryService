package com.practice.delivery.repository

import com.practice.delivery.entity.Order
import org.springframework.data.jpa.repository.JpaRepository

interface OrderRepository:JpaRepository<Order,Long> {
}