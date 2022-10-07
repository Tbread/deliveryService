package com.practice.delivery.model

import com.practice.delivery.entity.Menu

class OptionMenu {
    var name: String = ""
    var price: Int = 0

    constructor(menu:Menu){
        this.name = menu.menuName
        this.price = menu.price
    }
}