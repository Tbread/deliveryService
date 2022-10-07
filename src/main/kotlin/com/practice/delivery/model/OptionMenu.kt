package com.practice.delivery.model

import com.practice.delivery.entity.Menu

class OptionMenu {
    var id:Long? = null
    var name: String = ""
    var price: Int = 0

    constructor(menu:Menu){
        this.id = menu.id
        this.name = menu.menuName
        this.price = menu.price
    }

    constructor(name:String,price:Int){
        this.name = name
        this.price = price
    }
}