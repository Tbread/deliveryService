package com.practice.delivery.entity

import javax.persistence.*

@Entity
class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @Column(nullable = false)
    var categoryName:String = ""

    @Column(nullable = false)
    var imgSrc:String = ""

}