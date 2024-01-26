package pw.react.backend.web

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import pw.react.backend.models.domain.Reservation

@Serializable
data class ReservationDto(
    val flatId: String,
    val startDate: String,
    val endDate: String,
    val adults: Int,
    val children: Int,
    val pets: Int,
    val specialRequests: String? = null,
)

fun ReservationDto.toDomain(userId: Long) = Reservation(
    userId = userId,
    flatId = flatId,
    startDate = LocalDate.parse(startDate),
    endDate = LocalDate.parse(endDate),
    adults = adults,
    children = children,
    pets = pets,
    specialRequests = specialRequests
)

fun Reservation.toDto() =
    ReservationDto(flatId, startDate.toString(), endDate.toString(), adults, children, pets, specialRequests)
