package pw.react.backend.security.jwt.models

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty

data class JwtRequest(
    val email: @NotEmpty @Email String,
    val password: @NotEmpty String
)
