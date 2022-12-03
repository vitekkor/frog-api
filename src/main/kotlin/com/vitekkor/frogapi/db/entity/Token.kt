package com.vitekkor.frogapi.db.entity

import org.hibernate.annotations.SelectBeforeUpdate
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.OneToOne
import javax.persistence.Table

@Entity
@Table(name = "Tokens")
@SelectBeforeUpdate
data class Token(
    @Id var token: String = "",
    var requests: Long = 0,
    @OneToOne(mappedBy = "token")
    var user: User? = null
)
