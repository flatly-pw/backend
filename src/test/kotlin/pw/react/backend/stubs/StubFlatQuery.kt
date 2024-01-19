package pw.react.backend.stubs

import kotlinx.datetime.LocalDate
import pw.react.backend.models.domain.FlatQuery

fun stubFlatQuery(
    page: Int,
    pageSize: Int,
    city: String? = "Warsaw",
    country: String? = "Poland",
    startDate: LocalDate? = LocalDate(2030, 10, 1),
    endDate: LocalDate? = LocalDate(2030, 10, 11),
    beds: Int? = 3,
    bedrooms: Int? = 2,
    bathrooms: Int? = 1,
    people: Int? = 3,
    pets: Int? = 0
) = FlatQuery(page, pageSize, city, country, startDate, endDate, beds, bedrooms, bathrooms, people, pets)
