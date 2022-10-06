package com.practice.delivery.entity

import javax.persistence.*


@Entity
class StoreRegisterRequest {

    enum class Status{
        AWAIT,
        ACCEPTED,
        DENIED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @Column(nullable = false)
    var storeName:String = ""

    @Column(nullable = true)
    var storeDesc:String? = null

    @Column(nullable = true)
    var storeImgSrc:String? = null

    @JoinColumn
    @ManyToOne
    var owner:User? = null

    @Column(nullable = false)
    var minOrderPrice:Int = 0

    @Column(nullable = false)
    var status:Status = Status.AWAIT

    @JoinColumn
    @ManyToOne
    var approver:User? = null


    fun acceptRequest(user: User){
        this.status = Status.ACCEPTED
        this.approver = user
    }

    fun denyRequest(user: User){
        this.status = Status.DENIED
        this.approver = user
    }



}