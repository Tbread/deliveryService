package com.practice.delivery.repository.dslrepository

import com.practice.delivery.entity.QStoreRegisterRequest
import com.practice.delivery.entity.StoreRegisterRequest
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@Repository
class QStoreRegisterRequestRepository(private var jpaQueryFactory: JPAQueryFactory) {

    fun findByStatus(status: StoreRegisterRequest.Status): List<StoreRegisterRequest> {
        return jpaQueryFactory.selectFrom(QStoreRegisterRequest.storeRegisterRequest)
            .where(QStoreRegisterRequest.storeRegisterRequest.status.eq(status))
            .join(QStoreRegisterRequest.storeRegisterRequest.owner)
            .join(QStoreRegisterRequest.storeRegisterRequest.approver)
            .fetchJoin()
            .fetch()
    }

    fun findAll(): List<StoreRegisterRequest> {
        return jpaQueryFactory.selectFrom(QStoreRegisterRequest.storeRegisterRequest)
            .join(QStoreRegisterRequest.storeRegisterRequest.owner)
            .join(QStoreRegisterRequest.storeRegisterRequest.approver)
            .fetchJoin()
            .fetch()
    }

}