package com.practice.delivery.entity

import java.time.LocalDate
import javax.persistence.*

@Entity
class Banner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @Column(nullable = false)
    var imgSrc:String

    @Column(nullable = false)
    var src:String

    @ManyToOne
    @JoinColumn
    var user:User

    @Column(nullable = false)
    var expired:Boolean

    @Column(nullable = false)
    var expireDate:LocalDate


    constructor(imgSrc:String,src:String,user: User,expireDate:LocalDate){
        this.imgSrc = imgSrc
        this.src = src
        this.user = user
        this.expired = false
        this.expireDate = expireDate
    }
}