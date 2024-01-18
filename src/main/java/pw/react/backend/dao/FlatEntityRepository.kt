package pw.react.backend.dao

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import pw.react.backend.models.entity.FlatEntity

interface FlatEntityRepository : JpaRepository<FlatEntity, String>, JpaSpecificationExecutor<FlatEntity>
