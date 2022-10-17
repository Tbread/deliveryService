package com.practice.delivery.model

import com.practice.delivery.entity.Banner
import java.time.LocalDate

class SimpleBanner {
    var imgSrc: String
    var src: String
    var expireDate: LocalDate


    constructor(banner: Banner) {
        this.imgSrc = banner.imgSrc
        this.src = banner.src
        this.expireDate = banner.expireDate
    }
}