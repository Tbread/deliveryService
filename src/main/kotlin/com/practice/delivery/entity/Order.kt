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
class Order:Timestamped() {

    enum class Status {
        AWAIT,
        COOKING,
        DELIVERING,
        COMPLETE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @ManyToOne
    @JoinColumn
    var orderer:User? = null

    @Column(nullable = false)
    var status:Status = Status.AWAIT

    @ManyToOne
    @JoinColumn
    var usedCoupon:Coupon? = null

    @Column(nullable = false)
    var initialPrice:Int = 0

    @Column(nullable = false)
    var finalPrice:Int = 0


    fun updateStatus(status: Status){
        this.status = status
    }

}