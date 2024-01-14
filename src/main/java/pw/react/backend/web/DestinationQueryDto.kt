package pw.react.backend.web

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import pw.react.backend.models.domain.DestinationQuery

@Serializable
data class DestinationQueryDto(
    val city: String? = null,
    val country: String? = null,
    @SerialName("postal_code") val postalCode: String? = null,
) {

    fun toDomain() = DestinationQuery(city, country, postalCode)
}
