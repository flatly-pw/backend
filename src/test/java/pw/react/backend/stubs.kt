package pw.react.backend

import kotlinx.datetime.LocalDate
import pw.react.backend.models.domain.DestinationQuery
import pw.react.backend.models.domain.Flat
import pw.react.backend.models.domain.FlatQuery
import pw.react.backend.models.entity.FlatEntity
import pw.react.backend.web.DestinationQueryDto
import pw.react.backend.web.FlatQueryDto

fun stubFlat(
    id: String? = "1",
    description: String = "description ${id.orEmpty()}",
    area: Int = 10,
    bedrooms: Int = 1,
    bathrooms: Int = 2,
    capacity: Int = 3
) = Flat(description, area, bedrooms, bathrooms, capacity, id)

fun stubFlatEntity(
    id: String? = "1",
    description: String = "description ${id.orEmpty()}",
    area: Int = 10,
    bedrooms: Int = 1,
    bathrooms: Int = 2,
    capacity: Int = 3
) = FlatEntity(description, area, bedrooms, bathrooms, capacity, id)

fun stubFlatQueryDto(
    destinationQuery: DestinationQueryDto? = stubDestinationQueryDto(),
    startDate: String = "2030-10-01",
    endDate: String = "2030-10-11",
    beds: Int? = 3,
    bedrooms: Int? = 2,
    bathrooms: Int? = 1,
    adults: Int = 3,
    children: Int = 1,
    pets: Int = 0
) = FlatQueryDto(destinationQuery, startDate, endDate, beds, bedrooms, bathrooms, adults, children, pets)

fun stubFlatQuery(
    destinationQuery: DestinationQuery? = stubDestinationQuery(),
    startDate: LocalDate = LocalDate(2030, 10, 1),
    endDate: LocalDate = LocalDate(2030, 10, 11),
    beds: Int? = 3,
    bedrooms: Int? = 2,
    bathrooms: Int? = 1,
    adults: Int = 3,
    children: Int = 1,
    pets: Int = 0
) = FlatQuery(destinationQuery, startDate, endDate, beds, bedrooms, bathrooms, adults, children, pets)

fun stubDestinationQueryDto(
    city: String? = "Warsaw",
    country: String? = "Poland",
    postalCode: String? = null
) = DestinationQueryDto(city, country, postalCode)

fun stubDestinationQuery(
    city: String? = "Warsaw",
    country: String? = "Poland",
    postalCode: String? = null
) = DestinationQuery(city, country, postalCode)
