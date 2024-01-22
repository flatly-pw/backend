package pw.react.backend.dao

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import pw.react.backend.models.entity.ReservationEntity
import java.time.LocalDate

interface ReservationRepository : JpaRepository<ReservationEntity, Long> {

    @Query(
        """
        select count(reservation) from ReservationEntity reservation 
        where reservation.flat.id = ?1 and (
            reservation.startDate <= ?2 and ?2 < reservation.endDate 
            or
            reservation.startDate < ?3 and ?3 <= reservation.endDate)"""
    )
    fun countAllWithOverlappingDates(flatId: String, start: LocalDate, end: LocalDate): Int
}
