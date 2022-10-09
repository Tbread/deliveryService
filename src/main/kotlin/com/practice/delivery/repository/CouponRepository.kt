package com.practice.delivery.repository

import com.practice.delivery.entity.Coupon
import org.springframework.data.jpa.repository.JpaRepository

interface CouponRepository:JpaRepository<Coupon,Long> {

    fun findByExpired(expired:Boolean):List<Coupon>
}