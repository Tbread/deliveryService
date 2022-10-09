package com.practice.delivery.entity

import javax.persistence.Column
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @Column(nullable = false)
    var expired:Boolean = false

    @Column(nullable = false)
    var couponMaster:CouponMaster? = null

    @Column(nullable = false)
    var available:Boolean = true

    constructor(couponMaster: CouponMaster){
        this.couponMaster = couponMaster
        this.expired = false
        this.available = true
    }

    fun expireCoupon(){
        this.expired = true
        this.available = false
    }

    fun useCoupon(){
        this.available = false
    }
}