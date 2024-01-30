package pw.react.backend.stubs

import pw.react.backend.models.domain.Address
import pw.react.backend.models.domain.Flat
import pw.react.backend.models.domain.FlatOwner
import pw.react.backend.models.entity.AddressEntity
import pw.react.backend.models.entity.FlatEntity
import pw.react.backend.models.entity.FlatOwnerEntity

fun stubFlat(
    id: String? = "1",
    title: String = "Flat $id",
    description: String = "description ${id.orEmpty()}",
    area: Int = 10,
    beds: Int = 1,
    bedrooms: Int = 1,
    bathrooms: Int = 2,
    capacity: Int = 3,
    thumbnailUrl: String = "image://flat/$id",
    address: Address = stubAddress(),
    pricePerNight: Double = 100.0,
    rating: Float = 4.7f,
    type: String = "Hotel",
    facilities: List<String> = listOf("wi-fi"),
    owner: FlatOwner = stubFlatOwner()
) = Flat(
    title,
    description,
    thumbnailUrl,
    area,
    beds,
    bedrooms,
    bathrooms,
    capacity,
    type,
    address,
    owner,
    facilities,
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
    facilities: List<String> = listOf("wi-fi")
) = FlatEntity(title, description, area, beds, bedrooms, bathrooms, capacity, type, address, owner, id).apply {
    this.facilities = facilities.map { stubFlatFacilityEntity(name = it, this) }.toSet()
}
