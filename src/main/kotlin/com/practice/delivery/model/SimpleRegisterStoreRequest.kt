package com.practice.delivery.model

import com.practice.delivery.entity.StoreRegisterRequest

class SimpleRegisterStoreRequest {

    var id:Long = 0L
    var storeName:String = ""
    var applicantName:String = ""
    var storeDesc:String? = ""
    var storeImgSrc:String? = ""


    constructor(req:StoreRegisterRequest){
        this.id = req.id
        this.storeName = req.storeName
        this.applicantName = req.owner!!.email
        this.storeDesc = req.storeDesc
        this.storeImgSrc = req.storeImgSrc
    }

}