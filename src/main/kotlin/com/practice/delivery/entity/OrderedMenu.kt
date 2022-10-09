package com.practice.delivery.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
class OrderedMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @ManyToOne
    @JoinColumn
    var order:Order? = null

    @ManyToOne
    @JoinColumn
    var menu:Menu? = null

    @Column(nullable = false)
    var quantity:Int = 0


}