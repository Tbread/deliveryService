package com.practice.delivery.repository

import com.practice.delivery.entity.MasterCoupon
import org.springframework.data.jpa.repository.JpaRepository

interface MasterCouponRepository:JpaRepository<MasterCoupon,Long> {

}