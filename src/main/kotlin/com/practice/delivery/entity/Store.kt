package com.practice.delivery.entity

import com.practice.delivery.utils.Timestamped
import javax.persistence.*

@Entity
class Store:Timestamped() {

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
    var score:Float? = 0f


    fun updateMinOrderPrice(price:Int){
        this.minOrderPrice = price
    }

    fun updateStoreDesc(desc:String){
        this.storeDesc = desc
    }

    fun updateStoreImgSrc(src:String){
        this.storeImgSrc = src
    }

    fun updateScore(score:Float){
        this.score = score
    }

}