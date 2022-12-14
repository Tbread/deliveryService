package com.practice.delivery.entity

import com.practice.delivery.utils.Timestamped
import javax.persistence.*

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

    @ManyToOne
    @JoinColumn
    var owner:User? = null

    constructor(masterCoupon:MasterCoupon,user: User){
        this.masterCoupon = masterCoupon
        this.expired = false
        this.available = true
        this.owner = user
    }

    fun expireCoupon(){
        this.expired = true
        this.available = false
    }

    fun useCoupon(){
        this.available = false
    }

    fun cancelOrder(){
        this.available = true
    }
}