package pw.react.backend.models.domain

data class FlatQuery(
    val beds: Int?,
    val bedrooms: Int?,
    val bathrooms: Int?,
    val adults: Int,
    val children: Int,
    val pets: Int
)
