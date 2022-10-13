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
class Review:Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @ManyToOne
    @JoinColumn
    var user: User

    @ManyToOne
    @JoinColumn
    var store: Store

    @ManyToOne
    @JoinColumn
    var deliveryOrder: DeliveryOrder

    @Column(nullable = false)
    var contents: String

    @Column(nullable = true)
    var imgSrc: String?

    @Column(nullable = false)
    var score: Int

    @Column(nullable = false)
    var deleted: Boolean

    constructor(user:User,deliveryOrder: DeliveryOrder,contents:String,imgSrc:String?,score:Int){
        this.user = user
        this.store = deliveryOrder.store!!
        this.deliveryOrder = deliveryOrder
        this.contents = contents
        this.imgSrc = imgSrc
        this.score = score
        this.deleted = false
    }

    fun deleteReview(){
        this.deleted = true
    }


}