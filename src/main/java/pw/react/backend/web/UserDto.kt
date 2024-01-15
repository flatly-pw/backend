package pw.react.backend.web

import jakarta.validation.constraints.Email
import pw.react.backend.models.entity.UserEntity

data class UserDto(
    val id: Long,
    val name: String,
    val lastName: String,
    val password: String?,
    val email: @Email String?
) {

    fun toEntity() = UserEntity().also {
        it.id = id
        it.name = name
        it.lastName = lastName
        it.email = email
        it.password = password
    }

    companion object {

        @JvmStatic
        fun UserEntity.toDto() = UserDto(id, name, lastName, null, email)
    }
}

