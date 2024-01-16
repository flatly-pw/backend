package pw.react.backend.web

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReviewDto(
    val rating: Int,
    val review: String,
    @SerialName("reviewer_name") val reviewerName: String,
    val date: String,
)
