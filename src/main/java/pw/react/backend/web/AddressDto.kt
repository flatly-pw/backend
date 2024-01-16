package pw.react.backend.web

import kotlinx.serialization.Serializable

@Serializable
data class AddressDto(
    val street: String = "Krakowskie Przedmieście 42/44",
    val postalCode: String = "00-325",
    val city: String = "Warsaw",
    val country: String = "Poland",
    val latitude: Double = 21.015868759083475,
    val longitude: Double = 52.24221209113686,
)
