package com.practice.delivery.scheduler

import com.practice.delivery.entity.Coupon
import com.practice.delivery.entity.MasterCoupon
import com.practice.delivery.repository.CouponRepository
import com.practice.delivery.repository.MasterCouponRepository
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate


@Service
class CouponScheduler(
    private var couponRepository: CouponRepository,
    private var masterCouponRepository: MasterCouponRepository
) {

    @Scheduled(cron = "0 0 00 * * ?")
    @Transactional
    @Async
    fun expiringCoupon() {
        var masterList = masterCouponRepository.findByExpired(false)
        for (master:MasterCoupon in masterList){
            if (master.expiryDate!!.isBefore(LocalDate.now())){
                master.expiringMasterCoupon()
                var couponList = couponRepository.findByMasterCoupon(master)
                for (coupon:Coupon in couponList){
                    coupon.expireCoupon()
                }
            }
        }
    }


}