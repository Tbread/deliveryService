package com.practice.delivery.scheduler

import com.practice.delivery.entity.Coupon
import com.practice.delivery.entity.MasterCoupon
import com.practice.delivery.repository.dslRepository.QCouponRepository
import com.practice.delivery.repository.dslRepository.QMasterCouponRepository
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate


@Service
class CouponScheduler(
    private var qMasterCouponRepository: QMasterCouponRepository,
    private var qCouponRepository: QCouponRepository
) {

    @Scheduled(cron = "0 0 00 * * ?")
    @Transactional
    @Async
    fun expiringCoupon() {
        val masterList = qMasterCouponRepository.findByExpired(false)
        for (master:MasterCoupon in masterList){
            if (master.expiryDate!!.isBefore(LocalDate.now()) || master.quantity <= 0){
                master.expiringMasterCoupon()
                val couponList = qCouponRepository.findByMasterCoupon(master)
                for (coupon:Coupon in couponList){
                    coupon.expireCoupon()
                }
            }
        }
    }


}