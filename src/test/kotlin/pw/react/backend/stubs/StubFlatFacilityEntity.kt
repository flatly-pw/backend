package pw.react.backend.stubs

import pw.react.backend.models.entity.FlatEntity
import pw.react.backend.models.entity.FlatFacilityEntity

fun stubFlatFacilityEntity(
    name: String = "wi-fi",
    vararg flats: FlatEntity
) = FlatFacilityEntity(name, setOf(*flats))
