package com.practice.delivery.entity

import com.practice.delivery.utils.Timestamped
import javax.persistence.*

@Entity
class DeliveryOrder:Timestamped() {

    enum class Status {
        AWAIT,
        COOKING,
        DELIVERING,
        COMPLETE,
        CANCEL
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

    @ManyToOne
    @JoinColumn
    var store:Store? = null

    @Column(nullable = false)
    var reviewed:Boolean = false


    fun updateStatus(status: Status){
        this.status = status
    }

    fun createReview(){
        this.reviewed = true
    }


}