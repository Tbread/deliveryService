package com.practice.delivery.repository.dslrepository

import com.practice.delivery.entity.Menu
import com.practice.delivery.entity.QMenu
import com.practice.delivery.entity.Store
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@Repository
class QMenuRepository(private var jpaQueryFactory: JPAQueryFactory) {

    fun findMainMenuByStore(store: Store): List<Menu> {
        return jpaQueryFactory.selectFrom(QMenu.menu)
            .where(QMenu.menu.thisIsOption.eq(false))
            .where(QMenu.menu.store.eq(store))
            .join(QMenu.menu.store)
            .fetchJoin()
            .fetch()
    }

}