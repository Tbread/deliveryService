package com.practice.delivery.dto.response

import com.practice.delivery.model.SimpleBanner

class ViewBannerListResponseDto {
    var code: Int = 0
    var msg: String = ""
    var simpleBannerList:List<SimpleBanner>? = null
}