package com.vitekkor.frogapi.service

import com.vitekkor.frogapi.db.entity.Token
import com.vitekkor.frogapi.db.entity.User
import com.vitekkor.frogapi.db.repository.TokenRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import java.security.SecureRandom


@Service
class TokenService(private val tokenRepository: TokenRepository) {

    private val random = SecureRandom()

    private val n = 64
    fun generateToken(user: User): Token {
        val sb = StringBuilder(n)
        for (i in 0 until n) {
            val index = (alphanum.length * random.nextDouble()).toInt()
            sb.append(alphanum[index])
        }
        if (user.token != null) {
            val oldToken = checkNotNull(user.token)
            return oldToken.apply { token = sb.toString() }
        }
        return Token(token = sb.toString(), user = user).also { user.token = it }
    }

    suspend fun saveToken(token: Token) {
        withContext(Dispatchers.IO) {
            tokenRepository.saveAndFlush(token)
        }
    }

    suspend fun getToken(tokenString: String): Token? = withContext(Dispatchers.IO) {
        return@withContext tokenRepository.findByToken(tokenString)
    }

    companion object {
        private const val upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

        private val lower = upper.lowercase()

        private const val digits = "0123456789"

        private val alphanum = upper + lower + digits
    }
}
