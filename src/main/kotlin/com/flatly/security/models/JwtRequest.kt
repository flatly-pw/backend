package com.flatly.security.models

data class JwtRequest(
    val username: String,
    val password: String
)
