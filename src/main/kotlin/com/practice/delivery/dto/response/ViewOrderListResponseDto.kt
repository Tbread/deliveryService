package com.practice.delivery.dto.response

import com.practice.delivery.model.SimpleOrder

class ViewOrderListResponseDto {
    var code:Int = 0
    var msg:String = ""
    var simpleOrderList:List<SimpleOrder>? = null
}