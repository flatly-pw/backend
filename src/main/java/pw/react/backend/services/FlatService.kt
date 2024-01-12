package pw.react.backend.services

import org.springframework.data.domain.Pageable
import pw.react.backend.dao.FlatEntityRepository
import pw.react.backend.models.domain.toDomain
import pw.react.backend.models.entity.FlatEntity

class FlatService(private val flatEntityRepository: FlatEntityRepository) {

    fun findAll(pageable: Pageable) = flatEntityRepository.findAll(pageable).map(FlatEntity::toDomain)
}
