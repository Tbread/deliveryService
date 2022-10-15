package com.practice.delivery.entity

import javax.persistence.*

@Entity
class FavorStore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @ManyToOne
    @JoinColumn
    var user:User

    @ManyToOne
    @JoinColumn
    var store: Store

    constructor(user: User,store: Store){
        this.user = user
        this.store = store
    }

}