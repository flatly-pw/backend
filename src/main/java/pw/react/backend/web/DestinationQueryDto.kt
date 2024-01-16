package pw.react.backend.web

import kotlinx.serialization.Serializable
import pw.react.backend.models.domain.DestinationQuery

@Serializable
data class DestinationQueryDto(
    val city: String? = null,
    val country: String? = null,
    val postalCode: String? = null,
) {

    fun toDomain() = DestinationQuery(city, country, postalCode)
}
