package com.practice.delivery.repository

import com.practice.delivery.entity.MenuOption
import org.springframework.data.jpa.repository.JpaRepository

interface MenuOptionRepository:JpaRepository<MenuOption,Long> {
}