package com.practice.delivery.entity

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


    constructor(imgSrc:String,src:String,user: User){
        this.imgSrc = imgSrc
        this.src = src
        this.user = user
    }
}