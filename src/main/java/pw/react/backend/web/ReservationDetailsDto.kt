package pw.react.backend.web

data class ReservationDetailsDto(
    val reservationId: Long,
    val flatId: String,
    val addressDto: AddressDto,
    val startDate: String,
    val endDate: String,
    val phoneNumber: String,
    val email: String,
    val clientName: String,
    val clientLastName: String,
    val bedrooms: Int,
    val bathrooms: Int,
    val beds: Int,
    val facilities: List<String>,
    val adults: Int,
    val children: Int,
    val pets: Int,
    val price: Float,
    val specialRequests: String?,
    // active, passed, cancelled
    val status: String,
)
