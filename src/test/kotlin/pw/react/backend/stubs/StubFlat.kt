package pw.react.backend.stubs

import pw.react.backend.models.domain.Address
import pw.react.backend.models.domain.Flat
import pw.react.backend.models.entity.AddressEntity
import pw.react.backend.models.entity.FlatEntity
import pw.react.backend.models.entity.FlatOwnerEntity

fun stubFlat(
    id: String? = "1",
    title: String = "Flat $id",
    description: String = "description ${id.orEmpty()}",
    area: Int = 10,
    bedrooms: Int = 1,
    bathrooms: Int = 2,
    capacity: Int = 3,
    thumbnailUrl: String = "image://flat/$id",
    address: Address = stubAddress(),
    pricePerNight: Double = 100.0,
    rating: Float = 4.7f,
    type: String = "Hotel",
) = Flat(
    title,
    description,
    thumbnailUrl,
    area,
    bedrooms,
    bathrooms,
    capacity,
    type,
    address,
    rating,
    pricePerNight,
    id
)

fun stubFlatEntity(
    id: String? = "1",
    title: String = "Flat $id",
    description: String = "description ${id.orEmpty()}",
    area: Int = 10,
    beds: Int = 1,
    bedrooms: Int = 1,
    bathrooms: Int = 2,
    capacity: Int = 3,
    type: String = "Hotel",
    owner: FlatOwnerEntity = stubFlatOwnerEntity(),
    address: AddressEntity = stubAddressEntity(),
) = FlatEntity(title, description, area, beds, bedrooms, bathrooms, capacity, type, address, owner, id)
