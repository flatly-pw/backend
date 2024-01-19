package pw.react.backend.models.entity

data class FlatReview(
    val rating: Int,
    val review: String,
    val reviewerName: String,
    val date: String,
)
