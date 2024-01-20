package pw.react.backend.stubs

import pw.react.backend.models.domain.FlatImage
import pw.react.backend.models.entity.FlatEntity
import pw.react.backend.models.entity.FlatImageEntity

fun stubFlatImage(
    flatId: String = "1",
    imageId: String = "1",
    name: String = "image$imageId",
    type: String = "image/jpg",
    bytes: ByteArray = "$name.$type".toByteArray()
) = FlatImage(imageId, name, type, bytes)

fun stubFlatImageEntity(
    flat: FlatEntity,
    id: String = "1",
    ordinal: Int = 0,
    name: String = "image$id",
    type: String = "image/jpg",
    bytes: ByteArray = "$name.$type".toByteArray()
) = FlatImageEntity(flat, name, type, bytes, ordinal, id)
