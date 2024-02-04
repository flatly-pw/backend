package pw.react.backend.web

import kotlinx.serialization.Serializable

@Serializable
data class ChangePasswordDto(
    val currentPassword: String,
    val newPassword: String,
)

@Serializable
data class ChangeNameDto(
    val newName: String? = null,
    val newLastName: String? = null
)

@Serializable
data class ChangeMailDto(
    val newMail: String
)
