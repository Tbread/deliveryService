package com.practice.delivery.repository

import com.practice.delivery.entity.CouponIssuanceLog
import com.practice.delivery.entity.MasterCoupon
import com.practice.delivery.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface CouponIssuanceLogRepository:JpaRepository<CouponIssuanceLog,Long> {
    fun existsByUserAndMasterCoupon(user:User,masterCoupon: MasterCoupon):Boolean
}