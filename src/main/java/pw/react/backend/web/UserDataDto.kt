package pw.react.backend.web

import kotlinx.serialization.Serializable
import pw.react.backend.models.domain.User

@Serializable
data class UserDetailsDto(
    val name: String,
    val lastName: String,
    val email: String
)

fun User.toDetailsDto() = UserDetailsDto(name, lastName, email)
