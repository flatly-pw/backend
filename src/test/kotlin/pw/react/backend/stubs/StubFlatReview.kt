package pw.react.backend.stubs

import pw.react.backend.models.entity.FlatReviewEntity
import pw.react.backend.models.entity.FlatReviewId

fun stubFlatReviewEntity(
    flatId: String,
    userId: Long,
    review: String = "This is a fun place to stay at.",
    rating: Int = 5,
) = FlatReviewEntity(review, rating, FlatReviewId(flatId, userId))
