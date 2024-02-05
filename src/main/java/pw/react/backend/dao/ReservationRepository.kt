package pw.react.backend.dao

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import pw.react.backend.models.entity.ReservationEntity
import java.time.LocalDate

interface ReservationRepository : JpaRepository<ReservationEntity, Long>, JpaSpecificationExecutor<ReservationEntity> {

    @Query(
        """
        select count(reservation) from ReservationEntity reservation 
        where reservation.flat.id = ?1 and reservation.cancelled = false and (
            reservation.startDate <= ?2 and ?2 < reservation.endDate 
            or
            reservation.startDate < ?3 and ?3 <= reservation.endDate)"""
    )
    fun countAllActiveWithOverlappingDates(flatId: String, start: LocalDate, end: LocalDate): Int


    @Query(
        value = """
            select reservation from ReservationEntity reservation
            where reservation.user.id = ?1 and reservation.externalUserId = ?2
            order by reservation.startDate asc
        """
    )
    fun findAllExternalByUserId(userId: Long, externalUserId: Long, pageable: Pageable): Page<ReservationEntity>

    fun findAllByUserIdAndExternalUserIdIsNullOrderByStartDateAsc(
        userId: Long,
        pageable: Pageable
    ): Page<ReservationEntity>

    @Query(
        value = """
            select reservation from ReservationEntity reservation 
            where reservation.user.id = ?1 and reservation.externalUserId = ?3 
            and reservation.endDate >= ?2 and reservation.cancelled = false
            order by reservation.startDate asc
        """
    )
    fun findAllExternalActiveByUserId(
        userId: Long,
        today: LocalDate,
        externalUserId: Long,
        pageable: Pageable
    ): Page<ReservationEntity>

    @Query(
        value = """
            select reservation from ReservationEntity reservation 
            where reservation.user.id = ?1 and reservation.externalUserId = null 
            and reservation.endDate >= ?2 and reservation.cancelled = false
            order by reservation.startDate asc
        """
    )
    fun findAllActiveByUserId(
        userId: Long,
        today: LocalDate,
        pageable: Pageable
    ): Page<ReservationEntity>

    @Query(
        value = """
            select reservation from ReservationEntity reservation
            where reservation.user.id = ?1 and reservation.externalUserId = ?3 
            and reservation.endDate < ?2 and reservation.cancelled = false
            order by reservation.startDate asc
        """
    )
    fun findAllExternalPassedByUserId(
        userId: Long,
        today: LocalDate,
        externalUserId: Long,
        pageable: Pageable
    ): Page<ReservationEntity>

    @Query(
        value = """
            select reservation from ReservationEntity reservation
            where reservation.user.id = ?1 and reservation.externalUserId = null
            and reservation.endDate < ?2 and reservation.cancelled = false
            order by reservation.startDate asc
        """
    )
    fun findAllPassedByUserId(
        userId: Long,
        today: LocalDate,
        pageable: Pageable
    ): Page<ReservationEntity>

    @Query(
        value = """
            select reservation from ReservationEntity reservation 
            where reservation.user.id = ?1 and reservation.externalUserId = ?2 
            and reservation.cancelled = true
            order by reservation.startDate asc
        """
    )
    fun findAllExternalCancelledByUserId(
        userId: Long,
        externalUserId: Long?,
        pageable: Pageable
    ): Page<ReservationEntity>

    @Query(
        value = """
            select reservation from ReservationEntity reservation 
            where reservation.user.id = ?1 and reservation.cancelled = true and reservation.externalUserId = null 
            order by reservation.startDate asc
        """
    )
    fun findAllCancelledByUserId(userId: Long, pageable: Pageable): Page<ReservationEntity>

    @Query(
        value = """
            select reservation from ReservationEntity reservation
            where reservation.flat.id = ?1 and reservation.cancelled = false and (
                month(reservation.startDate) = ?2 and year(reservation.startDate) = ?3
                or
                month(reservation.endDate) = ?2 and year(reservation.endDate) = ?3
                or 
                year(reservation.startDate) < ?3 and year(reservation.endDate) = ?3 and month(reservation.endDate) > ?2
                or
                year(reservation.startDate) = ?3 and year(reservation.endDate) > ?3 and month(reservation.startDate) < ?2
                or
                year(reservation.startDate) = ?3 and year(reservation.startDate) = year(reservation.endDate) and month(reservation.startDate) < ?2 and month(reservation.endDate) > ?2 
                or
                year(reservation.startDate) < ?3 and year(reservation.endDate) > ?3
            )
            order by reservation.startDate asc
        """
    )
    fun getReservationsByFlatIdAndMonthYear(flatId: String, month: Int, year: Int): List<ReservationEntity>
}
