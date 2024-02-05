package pw.react.backend.stubs

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import pw.react.backend.models.domain.Reservation
import pw.react.backend.models.entity.FlatEntity
import pw.react.backend.models.entity.ReservationEntity
import pw.react.backend.models.entity.UserEntity
import pw.react.backend.web.ReservationDto

fun stubReservationEntity(
    user: UserEntity,
    flat: FlatEntity,
    startDate: LocalDate = LocalDate(2023, 1, 1),
    endDate: LocalDate = LocalDate(2023, 1, 10),
    adults: Int = 2,
    children: Int = 1,
    pets: Int = 0,
    specialRequests: String? = "Pink pillow",
    cancelled: Boolean = false,
    externalUserId: Long? = null,
    id: Long? = null
) = ReservationEntity(
    user = user,
    flat = flat,
    startDate = startDate.toJavaLocalDate(),
    endDate = endDate.toJavaLocalDate(),
    adults = adults,
    children = children,
    pets = pets,
    specialRequests = specialRequests,
    cancelled = cancelled,
    externalUserId = externalUserId,
    id = id
)

fun stubReservation(
    userId: Long,
    flatId: String,
    startDate: LocalDate = LocalDate(2023, 1, 1),
    endDate: LocalDate = LocalDate(2023, 1, 10),
    adults: Int = 2,
    children: Int = 1,
    pets: Int = 0,
    specialRequests: String? = "Pink pillow",
    cancelled: Boolean = false,
    externalUserId: Long? = null,
    id: Long? = null
) = Reservation(
    userId,
    flatId,
    startDate,
    endDate,
    adults,
    children,
    pets,
    specialRequests,
    cancelled,
    externalUserId,
    id
)

fun stubReservationDto(
    flatId: String,
    startDate: LocalDate = LocalDate(2023, 1, 1),
    endDate: LocalDate = LocalDate(2023, 1, 10),
    adults: Int = 2,
    children: Int = 1,
    pets: Int = 0,
    specialRequests: String? = "Pink pillow",
) = ReservationDto(flatId, startDate.toString(), endDate.toString(), adults, children, pets, specialRequests)
