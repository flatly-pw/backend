package pw.react.backend.web

import kotlinx.serialization.Serializable
import pw.react.backend.models.domain.Flat

@Serializable
data class FlatDto(
    val description: String,
    val area: Int,
    val bedrooms: Int,
    val bathrooms: Int,
    val capacity: Int,
    val id: String
)

fun Flat.toDto() = FlatDto(description, area, bedrooms, bathrooms, capacity, id!!)
