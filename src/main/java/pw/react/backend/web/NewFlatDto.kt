package pw.react.backend.web

import kotlinx.serialization.Serializable
import org.springframework.web.multipart.MultipartFile
import pw.react.backend.models.domain.Address
import pw.react.backend.models.domain.Flat
import pw.react.backend.models.domain.FlatOwner


@Serializable
data class NewFlatDto(
    val title: String,
    val description: String,
    val area: Int,
    val beds: Int,
    val bedrooms: Int,
    val bathrooms: Int,
    val capacity: Int,
    val type: String,
    val facilities: List<String>,
    val pricePerNight: Double,
    val street: String,
    val postalCode: String,
    val city: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val flatownername: String,
    val flatownerlastName: String,
    val flatowneremail: String,
    val flatownerphoneNumber: String,
)

fun NewFlatDto.toDomain(address: Address, owner: FlatOwner) = Flat(
    title = title,
    description = description,
    thumbnailUrl = "",
    area = area,
    beds = beds,
    bedrooms = bedrooms,
    bathrooms = bathrooms,
    capacity = capacity,
    type = type,
    address = address,
    owner = owner,
    facilities = facilities,
    rating = 0f,
    pricePerNight = pricePerNight,
)
