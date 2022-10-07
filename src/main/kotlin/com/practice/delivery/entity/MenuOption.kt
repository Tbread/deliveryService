package com.practice.delivery.entity

import javax.persistence.*

@Entity
class MenuOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @ManyToOne
    @JoinColumn
    var topMenu:Menu? = null

    @ManyToOne
    @JoinColumn
    var subMenu:Menu? = null
}