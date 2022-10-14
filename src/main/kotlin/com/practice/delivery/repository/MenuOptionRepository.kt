package com.practice.delivery.repository

import com.practice.delivery.entity.Menu
import com.practice.delivery.entity.MenuOption
import org.springframework.data.jpa.repository.JpaRepository

interface MenuOptionRepository:JpaRepository<MenuOption,Long> {

    fun findBySubMenu(subMenu:Menu):MenuOption?

}