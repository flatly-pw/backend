package pw.react.backend.models.entity

import pw.react.backend.models.domain.Address
import pw.react.backend.models.domain.FlatOwner

data class FlatDetails(
    val title: String,
    val thumbnail: String?,
    val gallery: List<String>,
    val rating: Float,
    val numberOfReviews: Int,
    val topReviews: List<FlatReview>,
    val area: Int,
    val beds: Int,
    val bedrooms: Int,
    val bathrooms: Int,
    val capacity: Int,
    val description: String,
    val address: Address,
    val owner: FlatOwner,
    val facilities: List<String>,
    val price: Double,
)
