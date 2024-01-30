package pw.react.backend.web

import kotlinx.serialization.Serializable
import pw.react.backend.utils.LocalDateRange

@Serializable
data class DatePeriodsDto(
    val data: List<DatePeriodDto>
)

@Serializable
data class DatePeriodDto(
    val startDate: String,
    val endDate: String
)

fun LocalDateRange.toDto() = DatePeriodDto(start.toString(), end.toString())
