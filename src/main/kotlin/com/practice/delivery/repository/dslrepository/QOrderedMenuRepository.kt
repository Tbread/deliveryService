package com.practice.delivery.repository.dslrepository

import com.practice.delivery.entity.DeliveryOrder
import com.practice.delivery.entity.OrderedMenu
import com.practice.delivery.entity.QOrderedMenu
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@Repository
class QOrderedMenuRepository(private var jpaQueryFactory: JPAQueryFactory) {

    fun findByOrder(order: DeliveryOrder): List<OrderedMenu> {
        return jpaQueryFactory.selectFrom(QOrderedMenu.orderedMenu)
            .where(QOrderedMenu.orderedMenu.deliveryOrder.eq(order))
            .join(QOrderedMenu.orderedMenu.menu)
            .join(QOrderedMenu.orderedMenu.deliveryOrder)
            .fetchJoin()
            .fetch()
    }

}