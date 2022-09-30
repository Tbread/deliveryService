package com.practice.delivery.entity

import com.practice.delivery.utils.Timestamped
import java.time.LocalDate
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

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

    @Column(nullable = false)
    var role:Role = Role.ROLE_DEFAULT

    fun updateOrderDate() {
        this.lastOrderDate = LocalDate.now()
    }

    fun updateLoginDate() {
        this.lastLoginDate = LocalDateTime.now()
    }


}