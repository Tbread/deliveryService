package com.practice.delivery.service.implemented

import com.practice.delivery.entity.User
import com.practice.delivery.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserDetailsServiceImpl(private var userRepository: UserRepository) : UserDetailsService {


    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(email: String): UserDetails {
        var user: User? = userRepository.findByEmail(email)
        if (Objects.isNull(user)) {
            throw UsernameNotFoundException("EmailNotFound")
        } else {
            return UserDetailsImpl(user!!)
        }
    }
}