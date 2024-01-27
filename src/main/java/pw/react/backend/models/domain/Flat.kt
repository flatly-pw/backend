package pw.react.backend.models.domain

data class Flat(
    val title: String,
    val description: String,
    val thumbnailUrl: String,
    val area: Int,
    val bedrooms: Int,
    val bathrooms: Int,
    val capacity: Int,
    val type: String,
    val address: Address,
    val rating: Float,
    val pricePerNight: Double,
    val id: String? = null,
)
