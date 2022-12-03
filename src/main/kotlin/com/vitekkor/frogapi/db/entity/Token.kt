package com.vitekkor.frogapi.db.entity

import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Parameter
import org.hibernate.annotations.SelectBeforeUpdate
import javax.persistence.*

@Entity
@Table(name = "Tokens")
@SelectBeforeUpdate
data class Token(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "token_sequence")
    @GenericGenerator(
        name = "token_sequence",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters =
        [
            Parameter(name = "token_sequence", value = "token_sequence"),
            Parameter(name = "increment_size", value = "1")
        ]
    )
    @Column(nullable = false)
    var id: Long? = null,
    @Column(nullable = false)
    var token: String = "",
    @Column(nullable = false)
    var requests: Long = 0,
    @OneToOne(mappedBy = "token")
    var user: User? = null
)
