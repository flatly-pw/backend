package pw.react.backend.web

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import pw.react.backend.models.domain.Flat

@Serializable
data class FlatDto(
    val id: String,
    val title: String,
    val thumbnail: String,
    val city: String,
    val rating: Float,
    @SerialName("price_per_night") val pricePerNight: Float,
)
