package pw.react.backend.models.domain

import pw.react.backend.models.entity.FlatEntity

data class Flat(
    val title: String,
    val description: String,
    val area: Int,
    val bedrooms: Int,
    val bathrooms: Int,
    val capacity: Int,
    val type: String,
    val id: String? = null,
)

fun FlatEntity.toDomain() = Flat(title, description, area, bedrooms, bathrooms, capacity, type, id)
