package pw.react.backend.web

import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import pw.react.backend.models.domain.FlatQuery

@Serializable
data class FlatQueryDto(
    val destination: DestinationQueryDto? = null,
    val startDate: String,
    val endDate: String,
    val beds: Int? = null,
    val bedrooms: Int? = null,
    val bathrooms: Int? = null,
    val adults: Int,
    val children: Int,
    val pets: Int
) {

    fun toDomain() = FlatQuery(
        destinationQuery = destination?.toDomain(),
        startDate = LocalDate.parse(startDate),
        endDate = LocalDate.parse(endDate),
        beds = beds,
        bedrooms = bedrooms,
        bathrooms = bathrooms,
        adults = adults,
        children = children,
        pets = pets
    )
}
