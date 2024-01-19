package pw.react.backend.models.entity

data class FlatDetails(
    val title: String,
    val area: Int,
    val beds: Int,
    val bedrooms: Int,
    val bathrooms: Int,
    val capacity: Int,
    val description: String,
    val address: Address,
) {

    data class Address(
        val street: String,
        val postalCode: String,
        val city: String,
        val country: String,
        val longitude: Double,
        val latitude: Double
    )
}

fun FlatEntity.toFlatDetails() = FlatDetails(
    title = title,
    area = area,
    beds = beds,
    bedrooms = bedrooms,
    bathrooms = bathrooms,
    capacity = capacity,
    description = description,
    address = with(address) {
        FlatDetails.Address(street, postalCode, city, country, longitude, latitude)
    }
)
