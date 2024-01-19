package pw.react.backend.stubs

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import pw.react.backend.models.entity.FlatEntity
import pw.react.backend.models.entity.FlatReviewEntity
import pw.react.backend.models.entity.FlatReviewId
import pw.react.backend.models.entity.UserEntity
import pw.react.backend.stubFlatEntity
import pw.react.backend.stubUserEntity

fun stubFlatReviewEntity(
    flatId: String,
    userId: Long,
    flat: FlatEntity = stubFlatEntity(id = flatId),
    user: UserEntity = stubUserEntity(id = userId),
    review: String = "This is a fun place to stay at.",
    rating: Int = 5,
    date: LocalDate = LocalDate(2023, 1, 1)
) = FlatReviewEntity(FlatReviewId(flatId, userId), flat, user, review, rating, date.toJavaLocalDate())
