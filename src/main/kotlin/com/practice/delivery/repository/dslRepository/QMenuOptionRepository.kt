package com.practice.delivery.repository.dslRepository

import com.practice.delivery.entity.Menu
import com.practice.delivery.entity.MenuOption
import com.practice.delivery.entity.QMenuOption
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@Repository
class QMenuOptionRepository(private var jpaQueryFactory: JPAQueryFactory) {

    fun findByMainMenu(mainMenu: Menu): List<MenuOption> {
        return jpaQueryFactory.selectFrom(QMenuOption.menuOption)
            .where(QMenuOption.menuOption.topMenu.eq(mainMenu))
            .join(QMenuOption.menuOption.topMenu)
            .join(QMenuOption.menuOption.subMenu)
            .fetchJoin()
            .fetch()
    }

}