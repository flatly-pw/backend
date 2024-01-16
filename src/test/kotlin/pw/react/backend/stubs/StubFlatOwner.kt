package pw.react.backend.stubs

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import pw.react.backend.models.entity.FlatOwnerEntity

fun stubFlatOwnerEntity(
    id: Long? = 1L,
    name: String = "Jan",
    lastName: String = "Kowalski",
    email: String = "$name.$lastName@mail.com".lowercase(),
    phoneNumber: String = "+48 123 456 789",
    registeredAt: LocalDate = LocalDate(2020, 1, 1)
) = FlatOwnerEntity(name, lastName, email, phoneNumber, registeredAt.toJavaLocalDate(), id)
