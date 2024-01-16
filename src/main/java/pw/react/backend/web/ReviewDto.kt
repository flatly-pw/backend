package pw.react.backend.web

import kotlinx.serialization.Serializable

@Serializable
data class ReviewDto(
    val rating: Int = 5,
    val review: String = "Great place to stay.",
    val reviewerName: String = "Janusz",
    val date: String = "2023-12-12",
)
