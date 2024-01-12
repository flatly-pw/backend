package pw.react.backend.models.domain

data class Flat(
    val description: String,
    val area: Int,
    val bedrooms: Int,
    val bathrooms: Int,
    val capacity: Int,
    val id: String? = null,
)
