package com.practice.delivery.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
class AdminUserRequest {

    enum class Status {
        AWAIT,
        DENIED,
        ACCEPTED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @Column(nullable = false)
    var password: String = ""

    @Column(nullable = false)
    var email: String = ""

    @Column(nullable = false)
    var status = AdminUserRequest.Status.AWAIT

    @ManyToOne
    @JoinColumn
    var approver:User? = null

    fun acceptRequest(user: User){
        this.status = AdminUserRequest.Status.ACCEPTED
        this.approver = user
    }

    fun denyRequest(user: User){
        this.status = AdminUserRequest.Status.DENIED
        this.approver = user
    }

}