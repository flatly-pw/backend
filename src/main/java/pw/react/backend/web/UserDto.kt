package pw.react.backend.web

import jakarta.validation.constraints.Email
import kotlinx.serialization.Serializable
import org.springframework.security.core.GrantedAuthority
import pw.react.backend.models.domain.User

@Serializable
data class UserDto(
    val id: Long? = null,
    val name: String,
    val lastName: String,
    val password: String?,
    val email: @Email String,
) {

    fun toDomain() = User(name, lastName, email, password!!, id, "user")
}
