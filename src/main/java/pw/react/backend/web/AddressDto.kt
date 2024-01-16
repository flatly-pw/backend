package pw.react.backend.web

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddressDto(
    val street: String,
    @SerialName("postal_code") val postalCode: String,
    val city: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
)
