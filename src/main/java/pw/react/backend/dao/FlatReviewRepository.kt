package pw.react.backend.dao

import org.hibernate.query.spi.Limit
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import pw.react.backend.models.entity.FlatReviewEntity
import pw.react.backend.models.entity.FlatReviewId

interface FlatReviewRepository : JpaRepository<FlatReviewEntity, FlatReviewId> {

    fun countFlatReviewEntitiesByFlatId(id: String): Int

    @Query("select avg(rating) from FlatReviewEntity where flat.id = ?1")
    fun getAverageRatingByFlatId(id: String): Double

    @Query("select e from FlatReviewEntity e where e.flat.id = ?1 order by e.rating desc limit 3")
    fun getTopReviewsByFlatId(id: String): List<FlatReviewEntity>
}
