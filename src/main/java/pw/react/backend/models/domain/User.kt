package pw.react.backend.models.domain

import pw.react.backend.models.entity.UserEntity

data class User(
    val name: String,
    val lastName: String,
    val email: String,
    val password: String,
    val id: Long? = null
)

fun User.toEntity() = UserEntity(name, lastName, email, password, id)

fun UserEntity.toDomain() = User(name, lastName, email, password, id)
