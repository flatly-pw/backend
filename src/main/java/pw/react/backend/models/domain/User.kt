package pw.react.backend.models.domain

import org.springframework.security.core.GrantedAuthority
import pw.react.backend.models.entity.UserEntity

data class User(
    val name: String,
    val lastName: String,
    val email: String,
    val password: String,
    val id: Long? = null,
    val authority: String
)

fun User.toEntity() = UserEntity(name, lastName, email, password, authority, id)

fun UserEntity.toDomain() = User(name, lastName, email, password, id, authority)
