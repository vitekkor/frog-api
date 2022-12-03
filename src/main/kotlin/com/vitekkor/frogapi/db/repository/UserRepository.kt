package com.vitekkor.frogapi.db.repository

import com.vitekkor.frogapi.db.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findOneByEmail(email: String): User?
}
