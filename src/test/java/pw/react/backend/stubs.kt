package pw.react.backend

import pw.react.backend.models.domain.Flat
import pw.react.backend.models.entity.FlatEntity
import pw.react.backend.models.entity.UserEntity
import pw.react.backend.web.UserDto

fun stubFlat(
    id: String? = "1",
    description: String = "description ${id.orEmpty()}",
    area: Int = 10,
    bedrooms: Int = 1,
    bathrooms: Int = 2,
    capacity: Int = 3
) = Flat(description, area, bedrooms, bathrooms, capacity, id)

fun stubFlatEntity(
    id: String? = "1",
    description: String = "description ${id.orEmpty()}",
    area: Int = 10,
    bedrooms: Int = 1,
    bathrooms: Int = 2,
    capacity: Int = 3
) = FlatEntity(description, area, bedrooms, bathrooms, capacity, id)

fun stubUserDto(
    id: Long? = null,
    name: String = "John",
    lastName: String = "Smith",
    password: String = "password123",
    email: String = "john.smith@mail.com"
) = UserDto(id, name, lastName, password, email)

fun stubUserEntity(
    id: Long? = 1L,
    name: String = "John",
    lastName: String = "Smith",
    password: String = "password123",
    email: String = "john.smith@mail.com"
) = UserEntity(name, lastName, email, password, id)
