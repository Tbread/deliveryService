package com.practice.delivery.repository.dslrepository

import com.practice.delivery.entity.MasterCoupon
import com.practice.delivery.entity.QMasterCoupon
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@Repository
class QMasterCouponRepository(private var jpaQueryFactory: JPAQueryFactory) {

    fun findByExpired(expired:Boolean):List<MasterCoupon>{
        return jpaQueryFactory.selectFrom(QMasterCoupon.masterCoupon)
            .where(QMasterCoupon.masterCoupon.expired.eq(expired))
            .join(QMasterCoupon.masterCoupon.issuer)
            .fetchJoin()
            .fetch()
    }

}