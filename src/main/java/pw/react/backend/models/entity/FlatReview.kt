package pw.react.backend.models.entity

import kotlinx.datetime.LocalDate

data class FlatReview(
    val rating: Int,
    val review: String,
    val reviewerName: String,
    val date: LocalDate,
)
