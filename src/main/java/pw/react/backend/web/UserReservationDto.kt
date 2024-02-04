package pw.react.backend.web

import kotlinx.serialization.Serializable

@Serializable
data class UserReservationDto(
    val flatId: String,
    val reservationId: Long,
    val title: String,
    val thumbnailUrl: String?,
    val city: String,
    val country: String,
    val startDate: String,
    val endDate: String,
    val totalPrice: Double,
)
