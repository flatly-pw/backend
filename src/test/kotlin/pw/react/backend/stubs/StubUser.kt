package pw.react.backend.stubs

import pw.react.backend.models.domain.User
import pw.react.backend.models.entity.UserEntity
import pw.react.backend.web.UserDto


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
