package com.practice.delivery.dto.response

import com.practice.delivery.model.SimpleCoupon

class CreateCouponResponseDto {
    var code:Int = 0
    var msg:String = ""
    var simpleCoupon:SimpleCoupon? = null
}