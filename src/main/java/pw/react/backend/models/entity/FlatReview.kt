package pw.react.backend.models.entity

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toKotlinLocalDate

data class FlatReview(
    val rating: Int,
    val review: String,
    val reviewerName: String,
    val date: LocalDate,
)

fun FlatReviewEntity.toDomain() = FlatReview(rating, review, user.name, date.toKotlinLocalDate())
