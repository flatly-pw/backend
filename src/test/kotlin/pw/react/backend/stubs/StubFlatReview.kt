package pw.react.backend.stubs

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import pw.react.backend.models.entity.FlatEntity
import pw.react.backend.models.entity.FlatReview
import pw.react.backend.models.entity.FlatReviewEntity
import pw.react.backend.models.entity.FlatReviewId
import pw.react.backend.models.entity.UserEntity

fun stubFlatReview(
    review: String = "This is a fun place to stay at.",
    rating: Int = 5,
    reviewerName: String = "John",
    date: LocalDate = LocalDate(2023, 1, 1)
) = FlatReview(rating, review, reviewerName, date)

fun stubFlatReviewEntity(
    flatId: String,
    userId: Long,
    flat: FlatEntity = stubFlatEntity(id = flatId),
    user: UserEntity = stubUserEntity(id = userId),
    review: String = "This is a fun place to stay at.",
    rating: Int = 5,
    date: LocalDate = LocalDate(2023, 1, 1)
) = FlatReviewEntity(FlatReviewId(flatId, userId), flat, user, review, rating, date.toJavaLocalDate())
