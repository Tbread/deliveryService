package com.practice.delivery.repository

import com.practice.delivery.entity.Banner
import org.springframework.data.jpa.repository.JpaRepository

interface BannerRepository:JpaRepository<Banner,Long> {
}