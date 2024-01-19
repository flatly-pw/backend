package pw.react.backend.stubs

import pw.react.backend.models.domain.Address
import pw.react.backend.models.domain.FlatOwner
import pw.react.backend.models.entity.FlatDetails
import pw.react.backend.models.entity.FlatReview
import pw.react.backend.web.FlatDetailsDto
import pw.react.backend.web.toDto

fun stubFlatDetails(
    title: String = "Flat 1",
    thumbnail: String = "image://flat/1",
    gallery: List<String> = listOf("image://flat/1", "image://flat/2"),
    rating: Float = 4.5f,
    numberOfReviews: Int = 2,
    topReviews: List<FlatReview> = listOf(
        stubFlatReview(reviewerName = "John"),
        stubFlatReview(reviewerName = "Anthony")
    ),
    description: String = "description 1",
    area: Int = 10,
    bedrooms: Int = 1,
    bathrooms: Int = 2,
    capacity: Int = 3,
    beds: Int = 1,
    address: Address = stubAddress(),
    owner: FlatOwner = stubFlatOwner(),
    facilities: List<String> = listOf("wi-fi"),
    price: Double = 20.0
) = FlatDetails(
    title = title,
    thumbnail = thumbnail,
    gallery = gallery,
    rating = rating,
    numberOfReviews = numberOfReviews,
    topReviews = topReviews,
    area = area,
    beds = beds,
    bedrooms = bedrooms,
    bathrooms = bathrooms,
    capacity = capacity,
    description = description,
    address = address,
    owner = owner,
    facilities = facilities,
    price = price
)

fun stubFlatDetailsDto(
    title: String = "Flat 1",
    thumbnail: String = "image://flat/1",
    gallery: List<String> = listOf("image://flat/1", "image://flat/2"),
    rating: Float = 4.5f,
    numberOfReviews: Int = 2,
    topReviews: List<FlatReview> = listOf(
        stubFlatReview(reviewerName = "John"),
        stubFlatReview(reviewerName = "Anthony")
    ),
    description: String = "description 1",
    area: Int = 10,
    bedrooms: Int = 1,
    bathrooms: Int = 2,
    capacity: Int = 3,
    beds: Int = 1,
    address: Address = stubAddress(),
    owner: FlatOwner = stubFlatOwner(),
    facilities: List<String> = listOf("wi-fi"),
    price: Double = 20.0
) = FlatDetailsDto(
    title = title,
    thumbnail = thumbnail,
    gallery = gallery,
    description = description,
    rating = rating,
    numberOfReviews = numberOfReviews,
    topReviews = topReviews.map { it.toDto() },
    area = area,
    beds = beds,
    bedrooms = bedrooms,
    bathrooms = bathrooms,
    capacity = capacity,
    address = address.toDto(),
    owner = owner.toDto(),
    facilities = facilities,
    price = price
)
