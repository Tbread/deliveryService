package com.practice.delivery.repository.dslRepository

import com.practice.delivery.entity.QStore
import com.practice.delivery.entity.Store
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class QStoreRepository(private var jpaQueryFactory: JPAQueryFactory) {

    fun searchWords(words: String): List<Store> {
        return jpaQueryFactory.selectFrom(QStore.store)
            .where(QStore.store.storeName.contains(words))
            .join(QStore.store.owner)
            .fetchJoin()
            .fetch()
    }

}