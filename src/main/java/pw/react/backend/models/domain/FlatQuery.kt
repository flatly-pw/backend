package pw.react.backend.models.domain

import kotlinx.datetime.LocalDate

data class FlatQuery(
    val page: Int,
    val pageSize: Int,
    val city: String? = null,
    val country: String? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val beds: Int? = null,
    val bedrooms: Int? = null,
    val bathrooms: Int? = null,
    val people: Int? = null,
    val pets: Int? = null
)
