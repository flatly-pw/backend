package pw.react.backend.models.domain

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import pw.react.backend.models.entity.FlatEntity
import pw.react.backend.models.entity.ReservationEntity
import pw.react.backend.models.entity.UserEntity

data class Reservation(
    val userId: Long,
    val flatId: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val adults: Int,
    val children: Int,
    val pets: Int,
    val specialRequests: String? = null,
    val id: Long? = null,
)

fun Reservation.toEntity(userEntity: UserEntity, flatEntity: FlatEntity) = ReservationEntity(
    user = userEntity,
    flat = flatEntity,
    startDate = startDate.toJavaLocalDate(),
    endDate = endDate.toJavaLocalDate(),
    adults = adults,
    children = children,
    pets = pets,
    specialRequests = specialRequests,
    id = id
)

fun ReservationEntity.toDomain() = Reservation(
    userId = user.id!!,
    flatId = flat.id!!,
    startDate = startDate.toKotlinLocalDate(),
    endDate = endDate.toKotlinLocalDate(),
    adults = adults,
    children = children,
    pets = pets,
    specialRequests = specialRequests,
    id = id
)

sealed interface ReservationFilter {
    data object All : ReservationFilter
    data object Active : ReservationFilter
    data object Passed : ReservationFilter
    data object Cancelled : ReservationFilter
}
