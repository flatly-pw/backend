package pw.react.backend.web

import kotlinx.serialization.Serializable
import pw.react.backend.models.entity.FlatReview

@Serializable
data class ReviewDto(
    val rating: Int = 5,
    val review: String = "Great place to stay.",
    val reviewerName: String = "Janusz",
    val date: String = "2023-12-12",
)

fun FlatReview.toDto() = ReviewDto(rating, review, reviewerName, date.toString())
