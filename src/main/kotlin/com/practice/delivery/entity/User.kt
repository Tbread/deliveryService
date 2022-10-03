package com.practice.delivery.entity

import com.practice.delivery.utils.Timestamped
import java.time.LocalDate
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class User : Timestamped() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @Column(nullable = false)
    var email: String = ""

    @Column(nullable = false)
    var password: String = ""

    @Column(nullable = true)
    var lastLoginDate: LocalDateTime? = null

    @Column(nullable = true)
    var lastOrderDate: LocalDate? = null

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role:Role = Role.DEFAULT

    fun updateOrderDate() {
        this.lastOrderDate = LocalDate.now()
    }

    fun updateLoginDate() {
        this.lastLoginDate = LocalDateTime.now()
    }

    fun getAuth(): String {
        return this.role.toString()
    }

    fun getAuthorities(): List<String>{
        var authoritiesList = arrayListOf<String>()
        when (this.role) {
            Role.SUPERIOR_ADMIN -> {
                authoritiesList.add(Role.SUPERIOR_ADMIN.toString())
                authoritiesList.add(Role.ADMIN.toString())
                authoritiesList.add(Role.BUSINESS.toString())
                authoritiesList.add(Role.DEFAULT.toString())
            }
            Role.ADMIN -> {
                authoritiesList.add(Role.ADMIN.toString())
                authoritiesList.add(Role.BUSINESS.toString())
                authoritiesList.add(Role.DEFAULT.toString())
            }
            Role.BUSINESS -> {
                authoritiesList.add(Role.BUSINESS.toString())
            }
            else -> {
                authoritiesList.add(Role.DEFAULT.toString())
            }
        }
        return authoritiesList
    }
}