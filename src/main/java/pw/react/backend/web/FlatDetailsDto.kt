package pw.react.backend.web

import kotlinx.serialization.Serializable
import pw.react.backend.models.entity.FlatDetails
import pw.react.backend.models.entity.FlatReview

@Serializable
data class FlatDetailsDto(
    val title: String,
    val thumbnail: String,
    val gallery: List<String>,
    val rating: Float,
    val numberOfReviews: Int,
    val topReviews: List<ReviewDto>,
    val area: Int,
    val beds: Int,
    val bedrooms: Int,
    val bathrooms: Int,
    val capacity: Int,
    val description: String,
    val facilities: List<String>,
    val address: AddressDto,
    val owner: OwnerDetailsDto,
    val price: Double,
)

fun FlatDetails.toDto() = FlatDetailsDto(
    title = title,
    thumbnail = thumbnail,
    gallery = gallery,
    rating = rating,
    numberOfReviews = numberOfReviews,
    topReviews = topReviews.map(FlatReview::toDto),
    area = area,
    beds = beds,
    bedrooms = bedrooms,
    bathrooms = bathrooms,
    capacity = capacity,
    description = description,
    address = address.toDto(),
    owner = owner.toDto(),
    facilities = facilities,
    price = price
)
