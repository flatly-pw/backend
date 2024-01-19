package pw.react.backend.dao

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import pw.react.backend.models.entity.FlatImageEntity
import pw.react.backend.models.entity.FlatImageId

interface FlatImageRepository : JpaRepository<FlatImageEntity, FlatImageId> {

    // We treat image with ordinal 0 as thumbnail
    @Query("select image from FlatImageEntity image where image.flat.id = ?1 and image.id.ordinal = 0")
    fun findThumbnailByFlatId(flatId: String): FlatImageEntity?

    @Query("select image from FlatImageEntity image where image.flat.id = ?1 order by image.id.ordinal asc")
    fun findFlatImageEntitiesByFlatId(flatId: String): List<FlatImageEntity>
}
