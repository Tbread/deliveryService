package com.practice.delivery.dto.response

import com.practice.delivery.model.SimpleRegisterAdminRequest

class ViewRegisterAdminRequestListResponseDto {

    var code: Int = 0
    var msg: String? = null
    var simpleRequestList:List<SimpleRegisterAdminRequest>? = null

}