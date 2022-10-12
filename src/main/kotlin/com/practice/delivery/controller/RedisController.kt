package com.practice.delivery.controller

import org.springframework.data.redis.core.HashOperations
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.annotation.Resource


@RestController
@RequestMapping("/redis")
class RedisController() {

    @Resource(name = "redisTemplate")
    lateinit var hashOpsScoreInfo:HashOperations<String,Long,Float>

    val SCORE_INFO = "SCORE_INFO"

    @GetMapping("/{key}/{value}")
    fun redisTest(@PathVariable key:String,@PathVariable value:String):String{
        hashOpsScoreInfo.put(SCORE_INFO,key.toLong(),value.toFloat())
        return hashOpsScoreInfo.get(SCORE_INFO,key.toLong()).toString()
    }
}