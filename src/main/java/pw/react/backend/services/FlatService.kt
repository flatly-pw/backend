package pw.react.backend.services

import au.com.console.jpaspecificationdsl.and
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import pw.react.backend.dao.FlatEntityRepository
import pw.react.backend.dao.specification.FlatEntitySpecification.hasBathrooms
import pw.react.backend.dao.specification.FlatEntitySpecification.hasBedrooms
import pw.react.backend.dao.specification.FlatEntitySpecification.hasCapacity
import pw.react.backend.models.domain.FlatQuery
import pw.react.backend.models.domain.toDomain
import pw.react.backend.models.entity.FlatEntity

class FlatService(private val flatEntityRepository: FlatEntityRepository) {

    fun findAll(pageable: Pageable) = flatEntityRepository.findAll(pageable).map(FlatEntity::toDomain)

    fun findAll(flatQuery: FlatQuery, pageable: Pageable) = flatEntityRepository
        .findAll(flatSpecification(flatQuery), pageable)
        .map(FlatEntity::toDomain)

    private fun flatSpecification(flatQuery: FlatQuery): Specification<FlatEntity> = with(flatQuery) {
        and(
            bedrooms?.let(::hasBedrooms),
            bathrooms?.let(::hasBathrooms),
            hasCapacity(adults + children),
        )
    }
}
