package com.practice.delivery.entity

import com.practice.delivery.utils.Timestamped
import javax.persistence.*


@Entity
class CouponIssuanceLog: Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @ManyToOne
    @JoinColumn
    var user:User? = null

    @ManyToOne
    @JoinColumn
    var masterCoupon:MasterCoupon? = null

    constructor(user:User,masterCoupon: MasterCoupon){
        this.user = user
        this.masterCoupon = masterCoupon
    }
}