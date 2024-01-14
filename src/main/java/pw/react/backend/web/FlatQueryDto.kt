package pw.react.backend.web

import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import pw.react.backend.models.domain.FlatQuery

@Serializable
data class FlatQueryDto(
    val destination: DestinationQueryDto? = null,
    @SerialName("start_date") val startDate: LocalDate,
    @SerialName("end_date") val endDate: LocalDate,
    val beds: Int? = null,
    val bedrooms: Int? = null,
    val bathrooms: Int? = null,
    val adults: Int,
    val children: Int,
    val pets: Int
) {

    fun toDomain() = FlatQuery(
        destinationQuery = destination?.toDomain(),
        startDate = startDate,
        endDate = endDate,
        beds = beds,
        bedrooms = bedrooms,
        bathrooms = bathrooms,
        adults = adults,
        children = children,
        pets = pets
    )
}
