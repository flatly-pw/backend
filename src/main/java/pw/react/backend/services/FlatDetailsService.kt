package pw.react.backend.services

import pw.react.backend.dao.FlatEntityRepository
import pw.react.backend.exceptions.FlatNotFoundException
import pw.react.backend.models.domain.toDomain
import pw.react.backend.models.entity.FlatDetails
import pw.react.backend.models.entity.FlatFacilityEntity
import kotlin.jvm.optionals.getOrNull

class FlatDetailsService(
    private val flatEntityRepository: FlatEntityRepository,
    private val flatReviewService: FlatReviewService,
    private val flatPriceService: FlatPriceService,
    private val flatImageService: FlatImageService,
) {

    // in the future there will be user preferences with e.g. currency
    fun getFlatDetailsById(id: String): FlatDetails {
        val flatEntity = flatEntityRepository.findById(id).getOrNull()
            ?: throw FlatNotFoundException("Flat with id: $id was not found")
        val address = flatEntity.address.toDomain()
        val owner = flatEntity.owner.toDomain()
        val facilities = flatEntity.facilities.map(FlatFacilityEntity::name)
        val reviewsNumber = flatReviewService.getNumberOfReviewByFlatId(id)
        val avgRating = flatReviewService.getRatingByFlatId(id)
        val topReviews = flatReviewService.getTopReviewsByFlatId(id)
        val price = flatPriceService.getPriceByFlatId(id)
        val thumbnail = flatImageService.getThumbnailUriByFlatId(id)
        val gallery = flatImageService.getImageUrisByFlatId(id)
        return with(flatEntity) {
            FlatDetails(
                title = title,
                thumbnail = thumbnail,
                gallery = gallery,
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
                price = price
            )
        }
    }
}
