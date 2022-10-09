package com.practice.delivery.entity

import com.practice.delivery.utils.Timestamped
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
class Order:Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @ManyToOne
    @JoinColumn
    var orderer:User? = null

    constructor(user: User){
        this.orderer = user
    }

}