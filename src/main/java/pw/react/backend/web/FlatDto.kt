package pw.react.backend.web

import pw.react.backend.models.domain.Flat

data class FlatDto(
    val description: String,
    val area: Int,
    val bedrooms: Int,
    val bathrooms: Int,
    val capacity: Int,
    val id: String
)

fun Flat.toDto() = FlatDto(description, area, bedrooms, bathrooms, capacity, id!!)
