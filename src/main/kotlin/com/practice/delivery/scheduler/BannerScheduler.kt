package com.practice.delivery.scheduler

import com.practice.delivery.repository.dslRepository.QBannerRepository
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class BannerScheduler(private var qBannerRepository: QBannerRepository) {

    @Scheduled(cron = "0 0 00 * * ?")
    @Transactional
    @Async
    fun expireBanner() {
        val bannerList = qBannerRepository.getLiveExpiredBannerList()
        for (banner in bannerList) {
            banner.expireBanner()
        }
    }

}