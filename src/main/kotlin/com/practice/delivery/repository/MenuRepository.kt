package com.practice.delivery.repository

import com.practice.delivery.entity.Menu
import org.springframework.data.jpa.repository.JpaRepository

interface MenuRepository:JpaRepository<Menu,Long> {
}