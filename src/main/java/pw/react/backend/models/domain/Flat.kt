package pw.react.backend.models.domain

import pw.react.backend.models.entity.FlatEntity

data class Flat(
    val description: String,
    val area: Int,
    val bedrooms: Int,
    val bathrooms: Int,
    val capacity: Int,
    val id: String? = null,
)

fun Flat.toEntity() = FlatEntity(description, area, bedrooms, bathrooms, capacity, id)

fun FlatEntity.toDomain() = Flat(description, area, bedrooms, bathrooms, capacity, id)
