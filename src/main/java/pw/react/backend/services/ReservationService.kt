package pw.react.backend.services

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import pw.react.backend.dao.FlatEntityRepository
import pw.react.backend.dao.ReservationRepository
import pw.react.backend.dao.UserRepository
import pw.react.backend.models.domain.Reservation
import pw.react.backend.models.domain.toDomain
import pw.react.backend.models.domain.toEntity

class ReservationService(
    private val reservationRepository: ReservationRepository,
    private val userRepository: UserRepository,
    private val flatRepository: FlatEntityRepository
) {

    fun canReserveFlat(flatId: String, startDate: LocalDate, endDate: LocalDate): Boolean {
        val overlappingReservationsCount = reservationRepository.countAllWithOverlappingDates(
            flatId,
            startDate.toJavaLocalDate(),
            endDate.toJavaLocalDate()
        )
        return overlappingReservationsCount == 0
    }

    fun saveReservation(reservation: Reservation): Reservation {
        val anyReservationOverlaps = !canReserveFlat(reservation.flatId, reservation.startDate, reservation.endDate)
        if (anyReservationOverlaps) throw IllegalArgumentException("Cannot make a reservation because term ${reservation.startDate} to ${reservation.endDate} overlaps another reservation")
        val reservationEntity = reservation.toEntity(
            userEntity = userRepository.findById(reservation.userId).get(),
            flatEntity = flatRepository.findById(reservation.flatId).get()
        )
        return reservationRepository.save(reservationEntity).toDomain()
    }
}
