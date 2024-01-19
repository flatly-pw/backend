package pw.react.backend.stubs

import pw.react.backend.models.domain.Address
import pw.react.backend.models.entity.AddressEntity

fun stubAddress(
    id: Long = 1L,
    street: String = "Krakowskie Przedmiescie $id",
    postalCode: String = "00-000",
    city: String = "Warsaw",
    country: String = "Poland",
    latitude: Double = 0.0,
    longitude: Double = 0.0,
) = Address(street, postalCode, city, country, longitude, latitude)

fun stubAddressEntity(
    id: Long = 1L,
    street: String = "Krakowskie Przedmiescie $id",
    postalCode: String = "00-000",
    city: String = "Warsaw",
    country: String = "Poland",
    latitude: Double = 0.0,
    longitude: Double = 0.0,
) = AddressEntity(street, postalCode, city, country, latitude, longitude, id)
