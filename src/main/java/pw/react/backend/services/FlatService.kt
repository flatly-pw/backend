package pw.react.backend.services

import kotlinx.datetime.toJavaLocalDate
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification
import pw.react.backend.dao.FlatEntityRepository
import pw.react.backend.models.domain.Flat
import pw.react.backend.models.domain.FlatQuery
import pw.react.backend.models.domain.toDomain
import pw.react.backend.models.entity.AddressEntity
import pw.react.backend.models.entity.FlatEntity
import pw.react.backend.models.entity.ReservationEntity


class FlatService(private val flatEntityRepository: FlatEntityRepository) {

    fun findAll(flatQuery: FlatQuery): Page<Flat> {
        val pageable = PageRequest.of(flatQuery.page, flatQuery.pageSize)
        return flatEntityRepository
            .findAll(flatSpecification(flatQuery), pageable)
            .map(FlatEntity::toDomain)
    }

    private fun flatSpecification(flatQuery: FlatQuery) = Specification<FlatEntity> { root, query, builder ->
        val destinationPredicates = if (flatQuery.city != null || flatQuery.country != null) {
            val address = root.join<FlatEntity, AddressEntity>("address")
            val cityPredicate = flatQuery.city?.let { builder.equal(address.get<String>("city"), it) }
            val countryPredicate = flatQuery.country?.let { builder.equal(address.get<String>("country"), it) }
            arrayOf(cityPredicate, countryPredicate)
        } else {
            emptyArray()
        }

        val termPredicate = if (flatQuery.startDate != null && flatQuery.endDate != null) {
            // Creating sub-query with the type that is returned from this sub-query. In this case its count as Long
            val subQuery = query.subquery(Long::class.java)
            // define table on which sub-query will be performed
            val subRoot = subQuery.from(ReservationEntity::class.java)

            val predicate = builder.and(
                builder.equal(subRoot.get<FlatEntity>("flat").get<String>("id"), root.get<String>("id")),
                builder.or(
                    builder.and(
                        builder.lessThanOrEqualTo(subRoot["startDate"], flatQuery.startDate.toJavaLocalDate()),
                        builder.greaterThan(subRoot["endDate"], flatQuery.startDate.toJavaLocalDate())
                    ),
                    builder.and(
                        builder.lessThan(subRoot["startDate"], flatQuery.endDate.toJavaLocalDate()),
                        builder.greaterThanOrEqualTo(subRoot["endDate"], flatQuery.endDate.toJavaLocalDate())
                    )
                )
            )
            subQuery.select(builder.count(subRoot)).where(predicate)
            builder.equal(subQuery, 0L)
        } else {
            null
        }
        val predicates = listOf(
            termPredicate,
            *destinationPredicates,
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
}
