package com.practice.delivery.entity

import com.practice.delivery.utils.Timestamped
import javax.persistence.*

@Entity
class Menu:Timestamped() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @Column(nullable = false)
    var menuName:String = ""

    @Column(nullable = true,name = "\"desc\"")
    var desc:String? = null

    @Column(nullable = false)
    var price:Int = 0

    @Column(nullable = true)
    var imgSrc:String? = null

    @Column(nullable = false)
    var thisHasOption:Boolean = false

    @Column(nullable = false)
    var thisIsOption:Boolean = false

    @Column(nullable = false)
    var thisIsSoldOut:Boolean = false

    @ManyToOne
    @JoinColumn
    var store:Store? = null


    fun updateSoldOut(boolean: Boolean){
        this.thisIsSoldOut = boolean
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

    fun updateThisHasOption(boolean: Boolean){
        this.thisHasOption = boolean
    }
}