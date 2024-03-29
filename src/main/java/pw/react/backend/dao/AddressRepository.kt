package pw.react.backend.dao

import org.springframework.data.jpa.repository.JpaRepository
import pw.react.backend.models.entity.AddressEntity

interface AddressRepository : JpaRepository<AddressEntity, String>