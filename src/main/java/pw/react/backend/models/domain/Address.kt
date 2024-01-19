package pw.react.backend.models.domain

data class Address(
    val street: String,
    val postalCode: String,
    val city: String,
    val country: String,
    val longitude: Double,
    val latitude: Double
)
