package com.practice.delivery.model

import com.practice.delivery.entity.MasterCoupon
import java.time.LocalDate

class SimpleCoupon {
    var id: Long = 0
    var discountRate:Int = 0
    var discountPrice:Int = 0
    var minSpend:Int = 0
    var maxDiscount:Int = 0
    var expired:Boolean = false
    var expiryDate: LocalDate? = null
    var quantity:Int = 0

    constructor(coupon: MasterCoupon){
        this.id = coupon.id
        this.discountRate = coupon.discountRate
        this.discountPrice = coupon.discountPrice
        this.minSpend = coupon.minSpend
        this.maxDiscount = coupon.maxDiscount
        this.expiryDate = coupon.expiryDate
        this.quantity = coupon.quantity
        this.expired = coupon.expired
    }
}