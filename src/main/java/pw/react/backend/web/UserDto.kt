package pw.react.backend.web

import jakarta.validation.constraints.Email
import pw.react.backend.models.UserEntity

data class UserDto(
    val id: Long,
    val name: String,
    val lastName: String,
    val password: String?,
    val email: @Email String?
) {
    companion object {
        @JvmStatic
        fun toDto(user: UserEntity) = UserDto(user.id, user.name, user.lastName, null, user.email)

        @JvmStatic
        fun toEntity(userDto: UserDto) = UserEntity().apply {
            id = userDto.id
            name = userDto.name
            email = userDto.email
            password = userDto.password
        }
    }
}

