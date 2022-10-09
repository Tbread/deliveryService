package com.practice.delivery.repository

import com.practice.delivery.entity.Coupon
import com.practice.delivery.entity.MasterCoupon
import org.springframework.data.jpa.repository.JpaRepository

interface CouponRepository:JpaRepository<Coupon,Long> {

    fun findByMasterCoupon(masterCoupon: MasterCoupon):List<Coupon>
}