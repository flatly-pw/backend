package pw.react.backend

import pw.react.backend.models.domain.Flat
import pw.react.backend.models.entity.FlatEntity

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
