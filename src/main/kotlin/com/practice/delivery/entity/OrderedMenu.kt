package com.practice.delivery.entity

import javax.persistence.*

@Entity
class OrderedMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @ManyToOne
    @JoinColumn
    var deliveryOrder:DeliveryOrder? = null

    @ManyToOne
    @JoinColumn
    var menu:Menu? = null

    @Column(nullable = false)
    var quantity:Int = 0


}