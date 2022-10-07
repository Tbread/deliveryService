package com.practice.delivery.model

import com.practice.delivery.entity.Menu

class SimpleMenu {
    var menuId:Long? = null
    var menuName:String = ""
    var price:Int = 0
    var desc:String? = null
    var imgSrc:String? = null
    var optionMenuList:List<OptionMenu>? = null

    constructor(mainMenu:Menu,subMenus:List<OptionMenu>?){
        this.menuId = mainMenu.id
        this.menuName = mainMenu.menuName
        this.price = mainMenu.price
        this.desc = mainMenu.desc
        this.imgSrc = mainMenu.imgSrc
        this.optionMenuList = subMenus
    }
}