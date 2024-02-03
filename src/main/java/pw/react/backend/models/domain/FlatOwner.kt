package pw.react.backend.models.domain

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import pw.react.backend.models.entity.FlatEntity
import pw.react.backend.models.entity.FlatOwnerEntity

data class FlatOwner(
    val name: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val registeredAt: LocalDate,
    val id: Long? = null,
)

fun FlatOwner.toEntity() = FlatOwnerEntity(
    name = name,
    lastName = lastName,
    email = email,
    phoneNumber = phoneNumber,
    registeredAt = registeredAt.toJavaLocalDate(),
)

fun FlatOwnerEntity.toDomain() = FlatOwner(name, lastName, email, phoneNumber, registeredAt.toKotlinLocalDate(), id!!)
