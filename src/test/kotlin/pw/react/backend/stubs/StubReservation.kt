package pw.react.backend.stubs

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import pw.react.backend.models.domain.Reservation
import pw.react.backend.models.entity.FlatEntity
import pw.react.backend.models.entity.ReservationEntity
import pw.react.backend.models.entity.UserEntity

fun stubReservationEntity(
    user: UserEntity,
    flat: FlatEntity,
    startDate: LocalDate = LocalDate(2023, 1, 1),
    endDate: LocalDate = LocalDate(2023, 1, 10),
    adults: Int = 2,
    children: Int = 1,
    pets: Int = 0,
    specialRequests: String? = "Pink pillow",
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
    id: Long? = null
) = Reservation(userId, flatId, startDate, endDate, adults, children, pets, specialRequests, id)