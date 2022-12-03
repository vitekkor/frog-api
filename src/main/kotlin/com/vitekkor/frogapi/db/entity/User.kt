package com.vitekkor.frogapi.db.entity

import org.hibernate.annotations.SelectBeforeUpdate
import javax.persistence.*

@Entity
@Table(name = "Users", uniqueConstraints = [UniqueConstraint(columnNames = ["email"])])
@SelectBeforeUpdate
data class User(
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long? = null,
    var name: String? = null,
    var email: String? = null,
    var password: String? = null,
    @OneToOne @JoinColumn(name = "token_id", referencedColumnName = "token")
    var token: Token? = null
)
