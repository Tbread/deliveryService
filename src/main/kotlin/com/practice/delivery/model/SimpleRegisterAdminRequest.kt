package com.practice.delivery.model

import com.practice.delivery.entity.AdminUserRequest
import java.time.LocalDateTime

class SimpleRegisterAdminRequest {
    var id:Long? = null
    var email: String? = null
    var status: AdminUserRequest.Status? = null

    constructor(req: AdminUserRequest) {
        this.id = req.id
        this.email = req.email
        this.status = req.status
    }
}