package com.practice.delivery.model

import com.practice.delivery.entity.Review

class SimpleReview {
    var reviewId:Long
    var orderId:Long
    var contents:String
    var imgSrc:String?
    var score:Int

    constructor(review:Review){
        this.reviewId = review.id
        this.orderId = review.deliveryOrder.id
        this.contents = review.contents
        this.imgSrc = review.imgSrc
        this.score = review.score
    }
}