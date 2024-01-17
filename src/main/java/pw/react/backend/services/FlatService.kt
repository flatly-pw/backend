package pw.react.backend.services

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import pw.react.backend.dao.FlatEntityRepository
import pw.react.backend.models.domain.Flat
import pw.react.backend.models.domain.FlatQuery
import pw.react.backend.models.domain.toDomain
import pw.react.backend.models.entity.FlatEntity
import pw.react.backend.utils.TimeProvider

class FlatService(
    private val flatEntityRepository: FlatEntityRepository,
    private val timeProvider: TimeProvider,
) {

    fun findAll(flatQuery: FlatQuery): Page<Flat> {
        requireValidPageParams(flatQuery.page, flatQuery.pageSize)
        requireValidDates(flatQuery)
        requireValidGuestNumbers(flatQuery)
        requireValidRoomParameters(flatQuery)
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
            builder.equal(root.get<Int>("capacity"), flatQuery.adults + flatQuery.children)
        ).mapNotNull { it }.toTypedArray()
        builder.and(*predicates)
    }

    private fun requireValidPageParams(page: Int, pageSize: Int) {
        require(page >= 0) { "page must not be less that 0" }
        require(pageSize > 0) { "page size must be greater than 0" }
    }

    private fun requireValidDates(query: FlatQuery) {
        require(query.startDate < query.endDate) { "startDate must be earlier than endDate." }
        val now = timeProvider().toLocalDateTime(TimeZone.currentSystemDefault())
        require(query.endDate > LocalDate(now.year, now.month, now.dayOfMonth)) { "endDate must be in the future." }
    }

    private fun requireValidGuestNumbers(query: FlatQuery) = with(query) {
        require(adults >= 0) { "adults must not be less than 0" }
        require(children >= 0) { "children must not be less than 0" }
        require(pets >= 0) { "pets must not be less than 0" }
        require(adults + children > 0) { "There must be at least one guest: adult or children" }
    }

    private fun requireValidRoomParameters(query: FlatQuery) = with(query) {
        beds?.let { require(it >= 0) { "beds number cannot be less than 0" } }
        bedrooms?.let { require(it >= 0) { "bedrooms number cannot be less than 0" } }
        bathrooms?.let { require(it >= 0) { "bathrooms number cannot be less than 0" } }
    }
}
