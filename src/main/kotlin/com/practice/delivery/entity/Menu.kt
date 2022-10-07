package com.practice.delivery.entity

import javax.persistence.*

@Entity
class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @Column(nullable = false)
    var menuName:String = ""

    @Column(nullable = true)
    var desc:String? = null

    @Column(nullable = false)
    var price:Int = 0

    @Column(nullable = true)
    var imgSrc:String? = null

    @Column(nullable = false)
    var hasOption:Boolean = false

    @Column(nullable = false)
    var isOption:Boolean = false

    @Column(nullable = false)
    var isSoldOut:Boolean = false


    fun updateSoldOut(boolean: Boolean){
        this.isSoldOut = boolean
    }

    fun updateImgSrc(imgSrc:String){
        this.imgSrc = imgSrc
    }

    fun updateDesc(desc:String){
        this.desc = desc
    }

    fun updateMenuName(name:String){
        this.menuName = name
    }

    fun updatePrice(price:Int){
        this.price = price
    }
}