package com.practice.delivery.scheduler

import com.practice.delivery.repository.StoreRepository
import com.practice.delivery.repository.dslRepository.QReviewRepository
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StoreScoreScheduler(
    private var qReviewRepository: QReviewRepository,
    private var storeRepository: StoreRepository
) {

    @Scheduled(cron = "0 0 00 * * ?")
    @Transactional
    @Async
    fun updateScore(){
        val storeList = storeRepository.findAll()
        for (store in storeList){
            store.updateScore(qReviewRepository.calculateScore(store).toFloat())
        }
    }


}