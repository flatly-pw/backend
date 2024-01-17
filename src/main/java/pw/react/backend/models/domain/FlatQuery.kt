package pw.react.backend.models.domain

import kotlinx.datetime.LocalDate

data class FlatQuery(
    val page: Int,
    val pageSize: Int,
    val city: String?,
    val country: String?,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val beds: Int?,
    val bedrooms: Int?,
    val bathrooms: Int?,
    val adults: Int?,
    val children: Int?,
    val pets: Int?
)
