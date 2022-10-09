package com.practice.delivery.entity

import com.practice.delivery.utils.Timestamped
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
class Coupon:Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @Column(nullable = false)
    var expired:Boolean = false

    @ManyToOne
    @JoinColumn
    var masterCoupon:MasterCoupon? = null

    @Column(nullable = false)
    var available:Boolean = true

    constructor(masterCoupon:MasterCoupon){
        this.masterCoupon = masterCoupon
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