package pw.react.backend.services

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification
import pw.react.backend.dao.FlatEntityRepository
import pw.react.backend.models.domain.Flat
import pw.react.backend.models.domain.FlatQuery
import pw.react.backend.models.domain.toDomain
import pw.react.backend.models.entity.FlatEntity

class FlatService(private val flatEntityRepository: FlatEntityRepository) {

    fun findAll(flatQuery: FlatQuery): Page<Flat> {
        val pageable = PageRequest.of(flatQuery.page, flatQuery.pageSize)
        return flatEntityRepository
            .findAll(flatSpecification(flatQuery), pageable)
            .map(FlatEntity::toDomain)
    }

    private fun flatSpecification(flatQuery: FlatQuery): Specification<FlatEntity> = Specification { root, _, builder ->
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
            builder.equal(root.get<Int>("capacity"), flatQuery.people)
        ).mapNotNull { it }.toTypedArray()
        builder.and(*predicates)
    }
}
