package com.practice.delivery.dto.response

import com.practice.delivery.model.SimpleMenu

class ShowMenuResponseDto {
    var code:Int = 0
    var msg:String = ""
    var simpleMenuList = arrayListOf<SimpleMenu>()
}