package pw.react.backend.models.domain

import pw.react.backend.models.entity.FlatEntity

data class Flat(
    val title: String,
    val description: String,
    val thumbnailUrl: String?,
    val area: Int,
    val beds: Int,
    val bedrooms: Int,
    val bathrooms: Int,
    val capacity: Int,
    val type: String,
    val address: Address,
    val owner: FlatOwner,
    val facilities: List<String>,
    val rating: Float,
    val pricePerNight: Double,
    val id: String? = null,
)

fun Flat.toEntity() = FlatEntity(
    title = title,
    description = description,
    area = area,
    beds = beds,
    bedrooms = bedrooms,
    bathrooms = bathrooms,
    capacity = capacity,
    type = type,
    address = address.toEntity(),
    owner = owner.toEntity(),
    id = id,
)

