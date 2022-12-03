package com.vitekkor.frogapi.db.entity

import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Parameter
import org.hibernate.annotations.SelectBeforeUpdate
import javax.persistence.*

@Entity
@Table(name = "Users", uniqueConstraints = [UniqueConstraint(columnNames = ["email"])])
@SelectBeforeUpdate
data class User(
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_sequence")
    @GenericGenerator(
        name = "user_sequence",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters =
        [
            Parameter(name = "user_sequence", value = "user_sequence"),
            Parameter(name = "increment_size", value = "1")
        ]
    )
    @Column(nullable = false)
    var id: Long? = null,
    @Column(nullable = false)
    var name: String? = null,
    @Column(nullable = false)
    var email: String? = null,
    @Column(nullable = false)
    var password: String? = null,
    @OneToOne @JoinColumn(name = "token_id", referencedColumnName = "id")
    var token: Token? = null
)
