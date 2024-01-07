package com.flatly.dao

import com.flatly.models.entities.TokenEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface TokenRepository : JpaRepository<TokenEntity, Long> {

    fun findByValue(token: String): Optional<TokenEntity>
}
