package pw.react.backend.services

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toLocalDateTime
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import pw.react.backend.dao.FlatEntityRepository
import pw.react.backend.dao.ReservationRepository
import pw.react.backend.dao.UserRepository
import pw.react.backend.exceptions.ReservationException
import pw.react.backend.models.domain.Reservation
import pw.react.backend.models.domain.ReservationFilter
import pw.react.backend.models.domain.toDomain
import pw.react.backend.models.domain.toEntity
import pw.react.backend.models.entity.ReservationEntity
import pw.react.backend.utils.TimeProvider

class ReservationService(
    private val reservationRepository: ReservationRepository,
    private val userRepository: UserRepository,
    private val flatRepository: FlatEntityRepository,
    private val timeProvider: TimeProvider,
) {

    fun saveReservation(reservation: Reservation): Reservation {
        val anyReservationOverlaps = !canReserveFlat(reservation.flatId, reservation.startDate, reservation.endDate)
        if (anyReservationOverlaps) throw ReservationException("Cannot make a reservation because term ${reservation.startDate} to ${reservation.endDate} overlaps another reservation")
        val reservationEntity = reservation.toEntity(
            userEntity = userRepository.findById(reservation.userId).get(),
            flatEntity = flatRepository.findById(reservation.flatId).get()
        )
        return reservationRepository.save(reservationEntity).toDomain()
    }

    fun getReservations(
        userId: Long,
        page: Int,
        pageSize: Int,
        filter: ReservationFilter = ReservationFilter.All
    ): Page<Reservation> {
        require(page >= 0) { "page must not be negative" }
        require(pageSize > 0) { "page must be positive" }
        val pageRequest = PageRequest.of(page, pageSize)
        return when (filter) {
            is ReservationFilter.All -> reservationRepository.findAllByUserId(userId, pageRequest)
            is ReservationFilter.Active -> reservationRepository.findAllActiveByUserId(
                userId = userId,
                today = timeProvider().toJavaLocalDate(),
                pageable = pageRequest
            )

            is ReservationFilter.Passed -> reservationRepository.findAllPassedByUserId(
                userId = userId,
                today = timeProvider().toJavaLocalDate(),
                pageable = pageRequest
            )

            else -> Page.empty()
        }.map(ReservationEntity::toDomain)
    }

    private fun canReserveFlat(flatId: String, startDate: LocalDate, endDate: LocalDate): Boolean {
        require(startDate < endDate) { "startDate must be earlier than endDate." }
        require(timeProvider().toLocalDateTime(TimeZone.currentSystemDefault()).date <= startDate) { "startDate must not be in the past" }
        val overlappingReservationsCount = reservationRepository.countAllWithOverlappingDates(
            flatId,
            startDate.toJavaLocalDate(),
            endDate.toJavaLocalDate()
        )
        return overlappingReservationsCount == 0
    }

    private fun Instant.toJavaLocalDate() = toLocalDateTime(TimeZone.currentSystemDefault()).date.toJavaLocalDate()
}
