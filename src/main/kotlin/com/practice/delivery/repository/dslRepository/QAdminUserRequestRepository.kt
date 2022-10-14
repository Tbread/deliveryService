package com.practice.delivery.repository.dslRepository

import com.practice.delivery.entity.AdminUserRequest
import com.practice.delivery.entity.QAdminUserRequest
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@Repository
class QAdminUserRequestRepository(private var jpaQueryFactory: JPAQueryFactory) {

    fun findAllByStatus(status: AdminUserRequest.Status): List<AdminUserRequest> {
        return jpaQueryFactory.selectFrom(QAdminUserRequest.adminUserRequest)
            .where(QAdminUserRequest.adminUserRequest.status.eq(status))
            .join(QAdminUserRequest.adminUserRequest.approver)
            .fetchJoin()
            .fetch()
    }

    fun findAll(): List<AdminUserRequest> {
        return jpaQueryFactory.selectFrom(QAdminUserRequest.adminUserRequest)
            .join(QAdminUserRequest.adminUserRequest.approver)
            .fetchJoin()
            .fetch()
    }
}