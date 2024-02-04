package pw.react.backend.web

import kotlinx.serialization.Serializable

@Serializable
data class ChangePasswordDto(
    val currentPassword: String,
    val newPassword: String,
)
