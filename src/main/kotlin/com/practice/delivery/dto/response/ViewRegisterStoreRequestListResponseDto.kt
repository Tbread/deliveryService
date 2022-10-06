package com.practice.delivery.dto.response


import com.practice.delivery.model.SimpleRegisterStoreRequest

class ViewRegisterStoreRequestListResponseDto {

    var code: Int = 0
    var msg: String? = null
    var simpleRequestList:List<SimpleRegisterStoreRequest>? = null
}