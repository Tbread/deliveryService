package com.practice.delivery.service.implement

import com.practice.delivery.entity.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserDetailsImpl(private var user: User) : UserDetails {

    fun userDetailsImpl(user: User) {
        this.user = user
    }

    fun getUser(): User {
        return user
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority>? {
        var authorities = arrayListOf<GrantedAuthority>()
        authorities.add(SimpleGrantedAuthority(user.getAuth()))
        return authorities
    }

    override fun getPassword(): String {
        return user.password
    }

    override fun getUsername(): String {
        return user.email
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }

}