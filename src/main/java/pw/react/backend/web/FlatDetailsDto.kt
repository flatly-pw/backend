package pw.react.backend.web

import kotlinx.serialization.Serializable
import pw.react.backend.models.entity.FlatDetails

@Serializable
data class FlatDetailsDto(
    val title: String,
    val thumbnail: String = "https://lh5.googleusercontent.com/p/AF1QipMv04BhvKSoihsfByh96Pi_-ME7mGQ_RWPMXoyA=w408-h281-k-no",
    val gallery: List<String> = listOf(
        "https://lh5.googleusercontent.com/p/AF1QipMv04BhvKSoihsfByh96Pi_-ME7mGQ_RWPMXoyA=w408-h281-k-no",
        "https://cf.bstatic.com/xdata/images/hotel/max1024x768/452957266.jpg?k=1aba1f772e5072b81c3b75836062cabc3ee160082eadba004f3a359abdaa17ab&o=&hp=1",
        "https://www.hotelbristolwarsaw.pl/resourcefiles/home-dining-images/belle-epoque.jpg?version=12082023161733",
        "https://dynamic-media-cdn.tripadvisor.com/media/photo-o/25/2a/e2/26/cafe-bristol-is-a-legendary.jpg?w=700&h=-1&s=1"
    ),
    val rating: Float = 4.7f,
    val numberOfReviews: Int = 123,
    val topReviews: List<ReviewDto> = listOf(ReviewDto(), ReviewDto(), ReviewDto()),
    val area: Int,
    val beds: Int,
    val bedrooms: Int,
    val bathrooms: Int,
    val capacity: Int,
    val description: String,
    val facilities: List<String> = listOf("Free wi-fi", "Paid breakfast", "Laundry service"),
    val address: AddressDto,
    val owner: OwnerDetailsDto = OwnerDetailsDto(),
    val price: Double = 999.95,
)

fun FlatDetails.toDto() = FlatDetailsDto(
    title = title,
    area = area,
    beds = beds,
    bedrooms = bedrooms,
    bathrooms = bathrooms,
    capacity = capacity,
    description = description,
    address = with(address) {
        AddressDto(street, postalCode, city, country, latitude, longitude)
    }
)
