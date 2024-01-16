package pw.react.backend.stubs

import pw.react.backend.models.entity.FlatEntity
import pw.react.backend.models.entity.FlatImageEntity

fun stubFlatImageEntity(
    flat: FlatEntity,
    id: String = "1",
    name: String = "image$id",
    type: String = "jpg",
    bytes: ByteArray = "$name.$type".toByteArray()
) = FlatImageEntity(flat, name, type, bytes, id)
