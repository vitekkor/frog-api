package com.vitekkor.frogapi.db.repository

import com.vitekkor.frogapi.db.entity.Token
import org.springframework.data.jpa.repository.JpaRepository

interface TokenRepository : JpaRepository<Token, Long> {
    fun findByToken(token: String): Token?
}
