package pw.react.backend.services;

import pw.react.backend.dao.FlatOwnerRepository
import pw.react.backend.models.domain.FlatOwner
import pw.react.backend.models.domain.toDomain
import pw.react.backend.models.domain.toEntity

class FlatOwnerService(private val flatOwnerRepository: FlatOwnerRepository){
    fun save(flatOwner: FlatOwner): FlatOwner{
        return flatOwnerRepository.save(flatOwner.toEntity()).toDomain()
    }
}
