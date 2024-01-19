package pw.react.backend.services

import pw.react.backend.dao.FlatEntityRepository
import pw.react.backend.exceptions.FlatNotFoundException
import pw.react.backend.models.domain.toDomain
import pw.react.backend.models.entity.FlatDetails
import pw.react.backend.models.entity.FlatFacilityEntity
import kotlin.jvm.optionals.getOrNull

class FlatDetailsService(
    private val flatEntityRepository: FlatEntityRepository,
    private val flatReviewService: FlatReviewService
) {

    fun getFlatDetailsById(id: String): FlatDetails {
        val flatEntity = flatEntityRepository.findById(id).getOrNull()
            ?: throw FlatNotFoundException("Flat with id: $id was not found")
        val address = flatEntity.address.toDomain()
        val owner = flatEntity.owner.toDomain()
        val facilities = flatEntity.facilities.map(FlatFacilityEntity::name)
        val reviewsNumber = flatReviewService.getNumberOfReviewByFlatId(id)
        val avgRating = flatReviewService.getRatingByFlatId(id)
        val topReviews = flatReviewService.getTopReviewsByFlatId(id)
        return with(flatEntity) {
            FlatDetails(
                title = title,
                rating = avgRating,
                numberOfReviews = reviewsNumber,
                topReviews = topReviews,
                area = area,
                beds = beds,
                bedrooms = bedrooms,
                bathrooms = bathrooms,
                capacity = capacity,
                description = description,
                address = address,
                owner = owner,
                facilities = facilities,
                price = price.priceDollars
            )
        }
    }
}
