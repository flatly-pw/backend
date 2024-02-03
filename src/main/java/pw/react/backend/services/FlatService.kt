package pw.react.backend.services

import kotlinx.datetime.toJavaLocalDate
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification
import pw.react.backend.dao.FlatEntityRepository
import pw.react.backend.models.domain.Flat
import pw.react.backend.models.domain.FlatQuery
import pw.react.backend.models.domain.toDomain
import pw.react.backend.models.domain.toEntity
import pw.react.backend.models.entity.AddressEntity
import pw.react.backend.models.entity.FlatEntity
import pw.react.backend.models.entity.ReservationEntity
import kotlin.jvm.optionals.getOrNull

class FlatService(
    private val flatEntityRepository: FlatEntityRepository,
    private val imageService: FlatImageService,
    private val reviewService: FlatReviewService,
    private val priceService: FlatPriceService,
) {

    fun findAll(flatQuery: FlatQuery): Page<Flat> {
        val pageable = PageRequest.of(flatQuery.page, flatQuery.pageSize)
        return flatEntityRepository
            .findAll(flatSpecification(flatQuery), pageable)
            .map { it.toDomain() }
    }

    fun findById(flatId: String): Flat? = flatEntityRepository.findById(flatId).getOrNull()?.toDomain()

    private fun FlatEntity.toDomain(): Flat {
        val flatId = id!!
        return Flat(
            id = flatId,
            title = title,
            description = description,
            thumbnailUrl = imageService.getThumbnailUriByFlatId(flatId),
            area = area,
            beds = beds,
            bedrooms = bedrooms,
            bathrooms = bathrooms,
            capacity = capacity,
            type = type,
            address = address.toDomain(),
            owner = owner.toDomain(),
            rating = reviewService.getRatingByFlatId(flatId),
            pricePerNight = priceService.getPriceByFlatId(flatId),
            facilities = facilities.map { it.name }
        )
    }

    private fun flatSpecification(flatQuery: FlatQuery) = roomsSpecification(flatQuery)
        .and(destinationSpecification(flatQuery))
        .and(notOverlappingDateSpecification(flatQuery))

    private fun roomsSpecification(flatQuery: FlatQuery) = Specification<FlatEntity> { root, _, builder ->
        val predicates = listOf(
            flatQuery.beds?.let {
                builder.equal(root.get<Int>("beds"), it)
            },
            flatQuery.bedrooms?.let {
                builder.equal(root.get<Int>("bedrooms"), it)
            },
            flatQuery.bathrooms?.let {
                builder.equal(root.get<Int>("bathrooms"), it)
            },
            flatQuery.people?.let {
                builder.equal(root.get<Int>("capacity"), it)
            },
        ).mapNotNull { it }.toTypedArray()
        builder.and(*predicates)
    }

    private fun destinationSpecification(flatQuery: FlatQuery) = Specification<FlatEntity> { root, _, builder ->
        val destinationPredicates = if (flatQuery.city != null || flatQuery.country != null) {
            val address = root.join<FlatEntity, AddressEntity>("address")
            val cityPredicate = flatQuery.city?.let { builder.equal(address.get<String>("city"), it) }
            val countryPredicate = flatQuery.country?.let { builder.equal(address.get<String>("country"), it) }
            arrayOf(cityPredicate, countryPredicate)
        } else {
            emptyArray()
        }.mapNotNull { it }.toTypedArray()
        builder.and(*destinationPredicates)
    }

    private fun notOverlappingDateSpecification(flatQuery: FlatQuery): Specification<FlatEntity>? {
        if (flatQuery.startDate == null || flatQuery.endDate == null) return null
        return Specification<FlatEntity> { root, query, builder ->
            // define sub-query and type that will be returned - in this case it's Long, because we return count from sub-query
            val subQuery = query.subquery(Long::class.java)
            // define table on which sub-query will be performed
            val reservation = subQuery.from(ReservationEntity::class.java)

            val equalFlatIds = builder.equal(
                reservation.get<FlatEntity>("flat").get<String>("id"),
                root.get<String>("id")
            )

            val startDate = flatQuery.startDate.toJavaLocalDate()
            val endDate = flatQuery.endDate.toJavaLocalDate()
            val doDatesOverlap = with(builder) {
                or(
                    and( // reservation.startDate <= startDate && startDate < reservation.endDate
                        lessThanOrEqualTo(reservation["startDate"], startDate),
                        greaterThan(reservation["endDate"], startDate)
                    ),
                    and( // reservation.startDate < endDate && endDate <= reservation.endDate
                        lessThan(reservation["startDate"], endDate),
                        greaterThanOrEqualTo(reservation["endDate"], endDate)
                    )
                )
            }

            val datesOverlapWithReservationForThisFlat = builder.and(equalFlatIds, doDatesOverlap)
            // count all the reservations that overlaps for each flat
            subQuery.select(builder.count(reservation)).where(datesOverlapWithReservationForThisFlat)
            // return all flats that do not have any overlapping dates, i.e count of these reservations is 0
            builder.equal(subQuery, 0L)
        }
    }

    fun saveNewFlat(flat: Flat):
            FlatEntity //potem zmienić na flat
    {
        //sprawdzenie czy nie to samo? bo żeby dwa razy nie dać tego samego, tylko po czym, tytule?
        val newFlatEntity = flat.toEntity()
        return flatEntityRepository.save(newFlatEntity)
            //.toDomain()
    }
}

