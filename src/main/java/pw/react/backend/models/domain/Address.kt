package pw.react.backend.models.domain

import pw.react.backend.models.entity.AddressEntity

data class Address(
    val street: String,
    val postalCode: String,
    val city: String,
    val country: String,
    val longitude: Double,
    val latitude: Double
)

fun Address.toEntity() = AddressEntity(
    street =street,
    postalCode = postalCode,
    city = city,
    country = country,
    longitude = longitude,
    latitude = latitude,
)
fun AddressEntity.toDomain() = Address(street, postalCode, city, country, longitude, latitude)

