package com.practice.delivery.repository.dslRepository

import com.practice.delivery.entity.Banner
import com.practice.delivery.entity.QBanner
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class QBannerRepository(private var jpaQueryFactory: JPAQueryFactory) {

    fun getLiveExpiredBannerList(): List<Banner> {
        return jpaQueryFactory.selectFrom(QBanner.banner)
            .where(QBanner.banner.expired.eq(false))
            .where(QBanner.banner.expireDate.before(LocalDate.now()))
            .join(QBanner.banner.user)
            .fetchJoin()
            .fetch()
    }

    fun getLiveBannerList(): List<Banner> {
        return jpaQueryFactory.selectFrom(QBanner.banner)
            .where(QBanner.banner.expired.eq(false))
            .join(QBanner.banner.user)
            .fetchJoin()
            .fetch()
    }

    fun getDeadBannerList(): List<Banner> {
        return jpaQueryFactory.selectFrom(QBanner.banner)
            .where(QBanner.banner.expired.eq(true))
            .join(QBanner.banner.user)
            .fetchJoin()
            .fetch()
    }

    fun getAll(): List<Banner> {
        return jpaQueryFactory.selectFrom(QBanner.banner)
            .join(QBanner.banner.user)
            .fetchJoin()
            .fetch()
    }

}