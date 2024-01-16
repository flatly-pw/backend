package pw.react.backend.web

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OwnerDetailsDto(
    val name: String = "Jan",
    @SerialName("last_name") val lastName: String = "Kowalski",
    val email: String = "jan.kowalski@mail.com",
    @SerialName("phone_number") val phoneNumber: String = "+48 123 456 789",
    @SerialName("registered_since") val registeredSince: String = "2024-01-01",
)
