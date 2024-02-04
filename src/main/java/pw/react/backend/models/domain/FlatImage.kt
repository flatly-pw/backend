package pw.react.backend.models.domain

import pw.react.backend.models.entity.FlatEntity
import pw.react.backend.models.entity.FlatImageEntity

data class FlatImage(
    val id: String,
    val name: String,
    val type: String,
    val bytes: ByteArray,
)

fun FlatImageEntity.toDomain() = FlatImage(id!!, fileName, fileType, bytes)

fun FlatImage.toEntity(flat: FlatEntity, ord: Int) = FlatImageEntity(
    flat = flat,
    fileName = name,
    fileType = type,
    bytes = bytes,

    ordinal = ord + 1
)
