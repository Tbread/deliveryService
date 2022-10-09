package com.practice.delivery.entity

import java.time.LocalDate
import javax.persistence.Column
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

class CouponMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @Column(nullable = false)
    var discountRate:Int = 0

    @Column(nullable = false)
    var discountPrice:Int = 0

    @Column(nullable = false)
    var minSpend:Int = 0

    @Column(nullable = false)
    var maxDiscount:Int = 0

    @ManyToOne
    @JoinColumn
    var issuer:User? = null

    @Column(nullable = false)
    var expiryDate: LocalDate? = null

    @Column(nullable = false)
    var quantity:Int = 0

    constructor(discountRate:Int,discountPrice:Int,minSpend:Int,maxDiscount:Int,issuer:User,expiryDate:LocalDate,quantity:Int){
        this.discountPrice = discountPrice
        this.discountRate = discountRate
        this.minSpend = minSpend
        this.maxDiscount = maxDiscount
        this.issuer = issuer
        this.expiryDate = expiryDate
        this.quantity = quantity
    }


}