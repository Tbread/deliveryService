package com.practice.delivery.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService

import org.springframework.stereotype.Component
import java.util.*
import javax.annotation.PostConstruct
import javax.servlet.http.HttpServletRequest

@Component
class JwtTokenProvider(private var userDetailsService: UserDetailsService) {


    @Value("\${etc.jwtkey}")
    lateinit var secretJwtKey: String

    val TOKEN_VALID_TIME: Long = 60 * 60 * 1000L

    @PostConstruct
    protected fun init() {
        secretJwtKey = Base64.getEncoder().encodeToString(secretJwtKey.toByteArray())
    }

    fun createToken(username: String, id: Long): String {
        val claims: Claims = Jwts.claims().setSubject(username)
        claims["userid"] = id.toString()
        val now: Date = Date()
        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(Date(now.time + TOKEN_VALID_TIME))
            .signWith(SignatureAlgorithm.HS256, secretJwtKey)
            .compact()
    }

    fun getUsername(token: String): String {
        return Jwts.parser().setSigningKey(secretJwtKey).parseClaimsJws(token).body.subject
    }

    fun getAuthentication(token: String): Authentication {
        var userDetails: UserDetails = userDetailsService.loadUserByUsername(this.getUsername(token))
        return UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
    }

    // 항상 헤더가 들어오는게 아니니 nullable
    fun resolveToken(req: HttpServletRequest): String? {
        return req.getHeader("Authorization")
    }

    fun validToken(token: String): Boolean {
        try {
            var claim: Jws<Claims> = Jwts.parser().setSigningKey(secretJwtKey).parseClaimsJws(token)
            return !claim.body.expiration.before(Date())
        } catch (e: Exception) {
            return false
        }
    }

}