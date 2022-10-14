package com.practice.delivery.dto.response

import com.practice.delivery.model.SimpleReview

class ViewReviewListResponseDto {
    var code:Int = 0
    var msg:String = ""
    var simpleReviewList:List<SimpleReview>? = null

}