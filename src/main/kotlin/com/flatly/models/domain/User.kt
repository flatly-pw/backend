package com.flatly.models.domain

import com.flatly.models.entities.UserEntity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

data class User(
    val id: Long,
    val username: String,
    val password: String,
    val email: String
) : UserDetails {

    override fun getUsername() = username

    override fun getPassword() = password

    override fun getAuthorities() = emptyList<GrantedAuthority>()

    override fun isAccountNonExpired() = true

    override fun isAccountNonLocked() = true

    override fun isCredentialsNonExpired() = true

    override fun isEnabled() = true
}

fun UserEntity.toDomain() = User(id, username, password, email)


