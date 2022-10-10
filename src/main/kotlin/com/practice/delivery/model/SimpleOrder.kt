package com.practice.delivery.model

import com.practice.delivery.entity.Order

class SimpleOrder {
    var orderId:Long = 0
    var menuNameList:List<String>? = null
    var quantityList:List<Int>? = null
    var priceSum:Int = 0
    var status:Order.Status? = null
}