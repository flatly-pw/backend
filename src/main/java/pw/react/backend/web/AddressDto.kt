package pw.react.backend.web

import kotlinx.serialization.Serializable
import pw.react.backend.models.domain.Address

@Serializable
data class AddressDto(
    val street: String,
    val postalCode: String,
    val city: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
)

fun Address.toDto() = AddressDto(street, postalCode, city, country, latitude, longitude)
