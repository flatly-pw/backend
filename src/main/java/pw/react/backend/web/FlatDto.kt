package pw.react.backend.web

import kotlinx.serialization.Serializable

@Serializable
data class FlatDto(
    val id: String,
    val title: String,
    val thumbnail: String = "https://static.prod.r53.tablethotels.com/media/hotels/slideshow_images_staged/large/1070616.jpg",
    val city: String = "Warsaw",
    val rating: Float = 4.7f,
    val distanceFromCenter: Float = 470f,
    val pricePerNight: Float = 999.95f,
)
