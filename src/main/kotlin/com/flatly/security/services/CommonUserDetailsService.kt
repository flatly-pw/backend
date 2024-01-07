package com.flatly.security.services

import com.flatly.dao.UserRepository
import com.flatly.models.domain.toDomain
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException

class CommonUserDetailsService(private val userRepository: UserRepository) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsername(username)?.toDomain()
        return user ?: throw UsernameNotFoundException("User not found with username: $username")
    }
}
