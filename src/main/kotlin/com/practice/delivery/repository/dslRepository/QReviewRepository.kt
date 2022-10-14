package com.practice.delivery.repository.dslRepository

import com.practice.delivery.entity.QReview
import com.practice.delivery.entity.Review
import com.practice.delivery.entity.Store
import com.practice.delivery.entity.User
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@Repository
class QReviewRepository(private val jpaQueryFactory: JPAQueryFactory) {

    fun calculateScore(store: Store):Double{
        return jpaQueryFactory.selectFrom(QReview.review)
            .where(QReview.review.createdAt.between(LocalDateTime.now().minusWeeks(1), LocalDateTime.now()))
            .where(QReview.review.store.eq(store))
            .where(QReview.review.deleted.eq(false))
            .select(QReview.review.score.avg())
            .fetchFirst()
    }

    fun getLiveStoreReviewList(store:Store):List<Review>{
        return jpaQueryFactory.selectFrom(QReview.review)
            .where(QReview.review.store.eq(store))
            .where(QReview.review.deleted.eq(false))
            .join(QReview.review.store)
            .join(QReview.review.user)
            .join(QReview.review.deliveryOrder)
            .fetchJoin()
            .fetch()
    }

    fun getLiveUserReviewList(user:User):List<Review>{
        return jpaQueryFactory.selectFrom(QReview.review)
            .where(QReview.review.user.eq(user))
            .where(QReview.review.deleted.eq(false))
            .join(QReview.review.store)
            .join(QReview.review.user)
            .join(QReview.review.deliveryOrder)
            .fetchJoin()
            .fetch()
    }

}