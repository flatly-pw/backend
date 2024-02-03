package pw.react.backend.dao

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import pw.react.backend.models.entity.FlatOwnerEntity

interface FlatOwnerRepository : JpaRepository<FlatOwnerEntity, String> {

    fun findByEmail(email: String): FlatOwnerEntity?
}