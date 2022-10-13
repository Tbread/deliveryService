package com.practice.delivery.scheduler

import com.practice.delivery.repository.StoreRepository
import com.practice.delivery.repository.dslrepository.QReviewRepository
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StoreScoreScheduler(
    private var qReviewRepository: QReviewRepository,
    private var storeRepository: StoreRepository
) {

    val SCORE_INFO = "SCORE_INFO"

    @Scheduled(cron = "0 0 00 * * ?")
    @Transactional
    @Async
    fun updateScore(){
        var storeList = storeRepository.findAll()
        for (store in storeList){
            store.updateScore(qReviewRepository.calculateScore(store).toFloat())
        }
    }


}