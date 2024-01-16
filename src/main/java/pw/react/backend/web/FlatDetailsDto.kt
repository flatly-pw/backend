package pw.react.backend.web

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FlatDetailsDto(
    val title: String,
    val thumbnail: String,
    val gallery: List<String>,
    val rating: Float,
    @SerialName("number_of_reviews") val numberOfReviews: Int,
    @SerialName("top_reviews") val topReviews: List<ReviewDto>,
    val area: Int,
    val beds: Int,
    val bedrooms: Int,
    val bathrooms: Int,
    val description: String,
    val facilities: List<String>,
    val address: AddressDto,
    @SerialName("owner") val owner: OwnerDetailsDto,
    val price: Double,
)
