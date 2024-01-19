package pw.react.backend.dao

import org.springframework.data.jpa.repository.JpaRepository
import pw.react.backend.models.entity.PriceEntity

interface FlatPriceRepository : JpaRepository<PriceEntity, String> {

    fun getPriceEntityByFlatId(id: String): PriceEntity
}
