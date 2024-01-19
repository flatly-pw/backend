package pw.react.backend.web

import kotlinx.serialization.Serializable

@Serializable
data class AddressDto(
    val street: String,
    val postalCode: String,
    val city: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
)
