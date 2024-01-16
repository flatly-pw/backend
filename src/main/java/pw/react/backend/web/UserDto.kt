package pw.react.backend.web

import jakarta.validation.constraints.Email
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import pw.react.backend.models.domain.User

@Serializable
data class UserDto(
    val id: Long? = null,
    val name: String,
    val lastName: String,
    val password: String?,
    val email: @Email String
) {

    fun toDomain() = User(name, lastName, email, password!!, id)

    companion object {

        @JvmStatic
        fun User.toDto() = UserDto(id!!, name, lastName, null, email)
    }
}

