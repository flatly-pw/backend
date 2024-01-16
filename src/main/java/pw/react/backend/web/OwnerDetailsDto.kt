package pw.react.backend.web

import kotlinx.serialization.Serializable

@Serializable
data class OwnerDetailsDto(
    val name: String = "Jan",
    val lastName: String = "Kowalski",
    val email: String = "jan.kowalski@mail.com",
    val phoneNumber: String = "+48 123 456 789",
    val registeredSince: String = "2024-01-01",
)
