package com.practice.delivery.repository

import com.practice.delivery.entity.CouponMaster
import org.springframework.data.jpa.repository.JpaRepository

interface CouponMasterRepository:JpaRepository<CouponMaster,Long> {
}