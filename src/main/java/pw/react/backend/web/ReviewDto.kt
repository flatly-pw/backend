package pw.react.backend.web

import kotlinx.serialization.Serializable
import pw.react.backend.models.entity.FlatReview

@Serializable
data class ReviewDto(
    val rating: Int ,
    val review: String,
    val reviewerName: String,
    val date: String,
)

fun FlatReview.toDto() = ReviewDto(rating, review, reviewerName, date.toString())
