package pw.react.backend.models

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import pw.react.backend.models.domain.FlatQuery
import pw.react.backend.utils.TimeProvider

class FlatQueryFactory(private val timeProvider: TimeProvider) {

    fun create(
        page: Int,
        pageSize: Int,
        city: String? = null,
        country: String? = null,
        startDateIso: String? = null,
        endDateIso: String? = null,
        beds: Int? = null,
        bedrooms: Int? = null,
        bathrooms: Int? = null,
        adults: Int? = null,
        children: Int? = null,
        pets: Int? = null
    ): FlatQuery {
        val startDate = startDateIso?.let(LocalDate::parse)
        val endDate = endDateIso?.let(LocalDate::parse)
        requireValidPagingParams(page, pageSize)
        requireValidDestinationParams(city, country)
        requireValidDates(startDate, endDate)
        requireValidRoomParameters(beds, bedrooms, bathrooms)
        requireValidGuestNumbers(adults, children, pets)
        val people = when {
            adults == null && children == null -> null
            else -> (adults ?: 0) + (children ?: 0)
        }
        return FlatQuery(
            page = page,
            pageSize = pageSize,
            city = city?.lowercase()?.trim(),
            country = country?.lowercase()?.trim(),
            startDate = startDate,
            endDate = endDate,
            beds = beds,
            bedrooms = bedrooms,
            bathrooms = bathrooms,
            people = people,
            pets = pets
        )
    }

    private fun requireValidPagingParams(page: Int, pageSize: Int) {
        require(page >= 0) { "page must not be less than 0" }
        require(pageSize > 0) { "page size must be greater than 0" }
    }

    private fun requireValidDestinationParams(city: String?, country: String?) {
        city?.let {
            require(city.isNotEmpty() && city.isNotBlank()) { "city is blank or empty" }
        }
        country?.let {
            require(country.isNotEmpty() && country.isNotBlank()) { "country is blank or empty" }
        }
    }

    private fun requireValidDates(startDate: LocalDate?, endDate: LocalDate?) {
        val bothNull = startDate == null && endDate == null
        val bothNotNull = startDate != null && endDate != null
        require(bothNull || bothNotNull) {
            "Both startDate and endDate must be either provided or not"
        }
        // for smart cast of startDate and endDate to LocalDate
        if (startDate != null && endDate != null) {
            require(startDate < endDate) { "startDate must be earlier than endDate." }
            val now = timeProvider().toLocalDateTime(TimeZone.currentSystemDefault())
            require(startDate >= LocalDate(now.year, now.month, now.dayOfMonth)) { "startDate must be in the future." }
        }
    }

    private fun requireValidGuestNumbers(adults: Int?, children: Int?, pets: Int?) {
        pets?.let {
            require(pets >= 0) { "pets must not be less than 0" }
        }
        when {
            adults != null && children != null -> {
                require(adults >= 0) { "Adults number must not be negative" }
                require(children >= 0) { "Children number must not be negative" }
                require(adults + children > 0) { "There must be at least one guest: adult or children" }
            }

            adults != null -> require(adults > 0) { "Adults number must be positive" }
            children != null -> require(children > 0) { "Children number must be positive" }
        }
    }

    private fun requireValidRoomParameters(beds: Int?, bedrooms: Int?, bathrooms: Int?) {
        beds?.let { require(it >= 0) { "beds number cannot be less than 0" } }
        bedrooms?.let { require(it >= 0) { "bedrooms number cannot be less than 0" } }
        bathrooms?.let { require(it >= 0) { "bathrooms number cannot be less than 0" } }
    }
}
