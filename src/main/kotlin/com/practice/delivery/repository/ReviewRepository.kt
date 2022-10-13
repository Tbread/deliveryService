package com.practice.delivery.repository

import com.practice.delivery.entity.Review
import org.springframework.data.jpa.repository.JpaRepository

interface ReviewRepository:JpaRepository<Review,Long> {
}