package com.practice.delivery.entity

import com.practice.delivery.utils.Timestamped
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class User :Timestamped() {

    enum class UserType{
        DEFAULT,
        BUISNESS,
        ADMIN,
        SUPERIOR_ADMIN
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id:Long = 0

    @Column(nullable = false)
    var username:String = ""

    @Column(nullable = false)
    var email:String = ""

    @Column(nullable = false)
    var password:String = ""



}