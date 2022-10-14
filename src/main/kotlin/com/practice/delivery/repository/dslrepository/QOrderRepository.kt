package com.practice.delivery.repository.dslrepository

import com.practice.delivery.entity.DeliveryOrder
import com.practice.delivery.entity.QDeliveryOrder
import com.practice.delivery.entity.Store
import com.practice.delivery.entity.User
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@Repository
class QOrderRepository(private var jpaQueryFactory: JPAQueryFactory) {

    fun findByStore(store: Store): List<DeliveryOrder> {
        return jpaQueryFactory.selectFrom(QDeliveryOrder.deliveryOrder)
            .where(QDeliveryOrder.deliveryOrder.store.eq(store))
            .join(QDeliveryOrder.deliveryOrder.orderer)
            .join(QDeliveryOrder.deliveryOrder.store)
            .join(QDeliveryOrder.deliveryOrder.usedCoupon)
            .fetchJoin()
            .fetch()
    }

    fun findByOrderer(orderer: User): List<DeliveryOrder> {
        return jpaQueryFactory.selectFrom(QDeliveryOrder.deliveryOrder)
            .where(QDeliveryOrder.deliveryOrder.orderer.eq(orderer))
            .join(QDeliveryOrder.deliveryOrder.orderer)
            .join(QDeliveryOrder.deliveryOrder.store)
            .join(QDeliveryOrder.deliveryOrder.usedCoupon)
            .fetchJoin()
            .fetch()
    }

}