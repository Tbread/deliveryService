package com.practice.delivery.repository.dslRepository

import com.practice.delivery.entity.Coupon
import com.practice.delivery.entity.MasterCoupon
import com.practice.delivery.entity.QCoupon
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@Repository
class QCouponRepository(private var jpaQueryFactory: JPAQueryFactory) {

    fun findByMasterCoupon(masterCoupon: MasterCoupon):List<Coupon>{
        return jpaQueryFactory.selectFrom(QCoupon.coupon)
            .where(QCoupon.coupon.masterCoupon.eq(masterCoupon))
            .join(QCoupon.coupon.masterCoupon)
            .join(QCoupon.coupon.owner)
            .fetchJoin()
            .fetch()
    }
}