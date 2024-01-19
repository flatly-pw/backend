package pw.react.backend.services

import pw.react.backend.dao.FlatReviewRepository
import pw.react.backend.models.entity.FlatReview
import pw.react.backend.models.entity.FlatReviewEntity
import pw.react.backend.models.entity.toDomain

class FlatReviewService(private val flatReviewRepository: FlatReviewRepository) {

    fun getNumberOfReviewByFlatId(flatId: String): Int {
        return flatReviewRepository.countFlatReviewEntitiesByFlatId(flatId)
    }

    fun getRatingByFlatId(flatId: String): Float {
        return flatReviewRepository.getAverageRatingByFlatId(flatId)
    }

    fun getTopReviewsByFlatId(flatId: String): List<FlatReview> {
        return flatReviewRepository.getTopReviewsByFlatId(flatId).map(FlatReviewEntity::toDomain)
    }

}
