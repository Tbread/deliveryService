package com.practice.delivery.repository.dslRepository

import com.practice.delivery.entity.FavorStore
import com.practice.delivery.entity.QFavorStore
import com.practice.delivery.entity.User
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class QFavorStoreRepository(private var jpaQueryFactory: JPAQueryFactory) {

    fun findByUser(user: User): List<FavorStore> {
        return jpaQueryFactory.selectFrom(QFavorStore.favorStore)
            .where(QFavorStore.favorStore.user.eq(user))
            .join(QFavorStore.favorStore.store)
            .join(QFavorStore.favorStore.user)
            .fetchJoin()
            .fetch()
    }

}