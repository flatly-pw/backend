package pw.react.backend.services

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toLocalDateTime
import pw.react.backend.dao.FlatEntityRepository
import pw.react.backend.dao.ReservationRepository
import pw.react.backend.dao.UserRepository
import pw.react.backend.exceptions.ReservationException
import pw.react.backend.models.domain.Reservation
import pw.react.backend.models.domain.toDomain
import pw.react.backend.models.domain.toEntity
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
}
