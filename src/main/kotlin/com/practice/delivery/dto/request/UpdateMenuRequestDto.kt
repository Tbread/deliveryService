package com.practice.delivery.dto.request

data class UpdateMenuRequestDto (
    val menuName:String?,
    val price:Int?,
    val soldOut:Boolean?,
    val imgSrc:String?,
    val desc:String?
)