package pw.react.backend.dao

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import pw.react.backend.models.entity.FlatImageEntity

interface FlatImageRepository : JpaRepository<FlatImageEntity, String> {

    // We treat image with ordinal 0 as thumbnail
    @Query("select image from FlatImageEntity image where image.flat.id = ?1 and image.ordinal = 0")
    fun findThumbnailByFlatId(flatId: String): FlatImageEntity?

    @Query("select image from FlatImageEntity image where image.flat.id = ?1 order by image.ordinal asc")
    fun findFlatImageEntitiesByFlatId(flatId: String): List<FlatImageEntity>

    @Query("select image from FlatImageEntity image where image.id = ?1 and image.flat.id = ?2")
    fun findFlatImageBy(imageId: String, flatId: String): FlatImageEntity?
}
