package pw.react.backend.web

data class ChangePasswordDto(
    val currentPassword: String,
    val newPassword: String,
)
