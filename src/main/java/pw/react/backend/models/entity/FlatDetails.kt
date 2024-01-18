package pw.react.backend.models.entity

data class FlatDetails(
    val title: String,
    val area: Int,
    val beds: Int,
    val bedrooms: Int,
    val bathrooms: Int,
    val capacity: Int,
    val description: String,
)
