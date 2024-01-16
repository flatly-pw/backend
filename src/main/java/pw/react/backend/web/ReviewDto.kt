package pw.react.backend.web

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReviewDto(
    val rating: Int = 5,
    val review: String = "Great place to stay.",
    @SerialName("reviewer_name") val reviewerName: String = "Janusz",
    val date: String = "2023-12-12",
)
