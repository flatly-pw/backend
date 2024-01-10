package pw.react.backend.web

import jakarta.validation.constraints.Email
import pw.react.backend.models.UserEntity

data class UserDto(
    val id: Long,
    val username: String,
    val password: String?,
    val email: @Email String?
) {
    companion object {
        @JvmStatic
        fun toDto(user: UserEntity): UserDto {
            return UserDto(user.id, user.username, null, user.email)
        }

        @JvmStatic
        fun toEntity(userDto: UserDto): UserEntity {
            val user = UserEntity()
            user.id = userDto.id
            user.username = userDto.username
            user.email = userDto.email
            user.password = userDto.password
            return user
        }
    }
}
