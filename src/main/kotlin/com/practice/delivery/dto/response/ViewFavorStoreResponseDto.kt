package com.practice.delivery.dto.response

import com.practice.delivery.model.SimpleStore

class ViewFavorStoreResponseDto {
    var code:Int = 0
    var msg:String = ""
    var simpleStoreList:List<SimpleStore>? = null
}