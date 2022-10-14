package com.practice.delivery.utils

import org.springframework.data.redis.core.ValueOperations
import org.springframework.web.servlet.HandlerInterceptor
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.TimeUnit
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class CustomInterceptor : HandlerInterceptor {

    @Resource(name = "redisTemplate")
    lateinit var valueOpsIpInfo: ValueOperations<String, Int>

    @Resource(name = "redisTemplate")
    lateinit var valueOpsBanInfo: ValueOperations<String, LocalDateTime>

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val ip = getIp(request)
        if (Objects.nonNull(ip)) {
            if (Objects.isNull(valueOpsBanInfo.get(ip!!))) {
                val cnt = valueOpsIpInfo.get(ip)
                if (Objects.isNull(cnt)) {
                    if (cnt!! >= 3) {
                        valueOpsBanInfo.set(ip, LocalDateTime.now().plusMinutes(5), 5, TimeUnit.MINUTES)
                        return false
                    } else
                        valueOpsIpInfo.set(ip, 1, 1, TimeUnit.SECONDS)
                } else {
                    valueOpsIpInfo.set(ip, 1 + cnt!!, 1, TimeUnit.SECONDS)
                }
            } else {
                return false
            }
        }
        return super.preHandle(request, response, handler)
    }

    fun getIp(request: HttpServletRequest): String? {
        var ip: String?
        ip = request.getHeader("X-Forwarded-For")
        if (ip == null || ip.isEmpty() || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.getHeader("Proxy-Client-IP")
        }
        if (ip == null || ip.isEmpty() || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.getHeader("WL-Proxy-Client-IP")
        }
        if (ip == null || ip.isEmpty() || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.getHeader("HTTP_CLIENT_IP")
        }
        if (ip == null || ip.isEmpty() || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR")
        }
        if (ip == null || ip.isEmpty() || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.getHeader("X-Real-IP")
        }
        if (ip == null || ip.isEmpty() || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.getHeader("X-RealIP")
        }
        if (ip == null || ip.isEmpty() || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.getHeader("REMOTE_ADDR")
        }
        if (ip == null || ip.isEmpty() || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.remoteAddr
        }
        return ip
    }
}