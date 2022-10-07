package com.practice.delivery.repository

import com.practice.delivery.entity.Menu
import com.practice.delivery.entity.Store
import org.springframework.data.jpa.repository.JpaRepository

interface MenuRepository:JpaRepository<Menu,Long> {
    fun existsByMenuName(menuName:String):Boolean

    fun findByStoreAndThisIsOption(store:Store,thisIsOption:Boolean):List<Menu>
}