package pw.react.backend.services

import pw.react.backend.dao.FlatEntityRepository
import pw.react.backend.exceptions.FlatNotFoundException
import pw.react.backend.models.domain.toDomain
import pw.react.backend.models.entity.FlatDetails
import kotlin.jvm.optionals.getOrNull

class FlatDetailsService(private val flatEntityRepository: FlatEntityRepository) {

    fun getFlatDetailsById(id: String): FlatDetails {
        val flatEntity = flatEntityRepository.findById(id).getOrNull()
            ?: throw FlatNotFoundException("Flat with id: $id was not found")
        val address = flatEntity.address.toDomain()
        val owner = flatEntity.owner.toDomain()
        return with(flatEntity) {
            FlatDetails(
                title = title,
                area = area,
                beds = beds,
                bedrooms = bedrooms,
                bathrooms = bathrooms,
                capacity = capacity,
                description = description,
                address = address,
                owner = owner
            )
        }
    }
}
