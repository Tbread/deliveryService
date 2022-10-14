package com.practice.delivery.jwt

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.GenericFilterBean
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

class JwtAuthenticationFilter(private var jwtTokenProvider: JwtTokenProvider) : GenericFilterBean() {

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        var token: String? = jwtTokenProvider.resolveToken(request as HttpServletRequest)
        if (Objects.nonNull(token)) {
            if ("bearer " in token!! || "Bearer " in token || "BEARER " in token) {
                token = token.substring(7)
            }
            if (jwtTokenProvider.validToken(token)) {
                val authentication: Authentication = jwtTokenProvider.getAuthentication(token)
                SecurityContextHolder.getContext().authentication = authentication
            }
        }
        chain.doFilter(request, response)
    }

}