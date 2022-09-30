package com.practice.delivery.service

import com.practice.delivery.entity.User
import com.practice.delivery.repository.userRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserDetailsServiceImpl(private var userRepository: userRepository) : UserDetailsService {


    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        var user: User? = userRepository.findByUsername(username)
        if (Objects.isNull(user)) {
            throw UsernameNotFoundException("UsernameNotFound")
        } else {
            return UserDetailsImpl(user!!)
        }
    }
}