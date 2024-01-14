package pw.react.backend.web

import kotlinx.serialization.Serializable
import pw.react.backend.models.domain.FlatQuery

@Serializable
data class FlatQueryDto(
    val beds: Int? = null,
    val bedrooms: Int? = null,
    val bathrooms: Int? = null,
    val adults: Int,
    val children: Int,
    val pets: Int
) {

    fun toDomain() = FlatQuery(beds, bedrooms, bathrooms, adults, children, pets)
}
