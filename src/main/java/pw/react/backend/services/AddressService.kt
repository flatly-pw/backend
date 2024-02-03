package pw.react.backend.services

import pw.react.backend.dao.AddressRepository
import pw.react.backend.models.domain.Address
import pw.react.backend.models.domain.toDomain
import pw.react.backend.models.domain.toEntity

class AddressService(private val addressRepository: AddressRepository) {
    fun save(address: Address): Address{
        return addressRepository.save(address.toEntity()).toDomain()
    }
}