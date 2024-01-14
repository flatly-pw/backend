package pw.react.backend.models.domain

import kotlinx.datetime.LocalDate

data class FlatQuery(
    val destinationQuery: DestinationQuery?,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val beds: Int?,
    val bedrooms: Int?,
    val bathrooms: Int?,
    val adults: Int,
    val children: Int,
    val pets: Int
)
