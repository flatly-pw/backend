package pw.react.backend

import pw.react.backend.models.domain.Address
import pw.react.backend.models.domain.Flat
import pw.react.backend.models.domain.User
import pw.react.backend.models.entity.AddressEntity
import pw.react.backend.models.entity.FlatEntity
import pw.react.backend.models.entity.FlatOwnerEntity
import pw.react.backend.models.entity.UserEntity
import pw.react.backend.stubs.stubAddress
import pw.react.backend.stubs.stubAddressEntity
import pw.react.backend.stubs.stubFlatOwnerEntity
import pw.react.backend.web.UserDto

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

fun stubUserDto(
    id: Long? = null,
    name: String = "John",
    lastName: String = "Smith",
    password: String = "password123",
    email: String = "john.smith@mail.com"
) = UserDto(id, name, lastName, password, email)

fun stubUser(
    id: Long? = null,
    name: String = "John",
    lastName: String = "Smith",
    password: String = "password123",
    email: String = "john.smith@mail.com"
) = User(name, lastName, email, password, id)

fun stubUserEntity(
    id: Long? = 1L,
    name: String = "John",
    lastName: String = "Smith",
    password: String = "password123",
    email: String = "john.smith@mail.com"
) = UserEntity(name, lastName, email, password, id)
