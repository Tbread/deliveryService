package com.practice.delivery.model

import com.practice.delivery.entity.Store

class SimpleStore {
    var id:Long
    var storeName:String
    var storeImgSrc:String?
    var score:Float?

    constructor(store:Store){
        this.id = store.id
        this.storeName = store.storeName
        this.storeImgSrc = store.storeImgSrc
        this.score = store.score
    }
}