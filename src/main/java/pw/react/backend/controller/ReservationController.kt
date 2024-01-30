package pw.react.backend.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.servlet.http.HttpServletRequest
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pw.react.backend.exceptions.FlatNotFoundException
import pw.react.backend.exceptions.ReservationException
import pw.react.backend.exceptions.ReservationNotFoundException
import pw.react.backend.models.domain.ReservationFilter
import pw.react.backend.security.jwt.services.JwtTokenService
import pw.react.backend.services.FlatPriceService
import pw.react.backend.services.FlatService
import pw.react.backend.services.ReservationService
import pw.react.backend.services.UserService
import pw.react.backend.utils.TimeProvider
import pw.react.backend.web.PageDto
import pw.react.backend.web.ReservationDetailsDto
import pw.react.backend.utils.LocalDateRange
import pw.react.backend.web.DatePeriodsDto
import pw.react.backend.web.ReservationDto
import pw.react.backend.web.UserReservationDto
import pw.react.backend.web.toDomain
import pw.react.backend.web.toDto

@RestController
class ReservationController(
    private val flatService: FlatService,
    private val reservationService: ReservationService,
    private val flatPriceService: FlatPriceService,
    private val jwtTokenService: JwtTokenService,
    private val userService: UserService,
    private val timeProvider: TimeProvider,
) {
    @Operation(
        summary = "Post new reservation",
        description = "Tries to place new reservation. If the given term is invalid " +
                "or already taken then appropriate status code is going to be returned."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Successfully placed new reservation",
        content = [
            Content(mediaType = "application/json", schema = Schema(oneOf = [ReservationDto::class]))
        ]
    )
    @ApiResponse(
        responseCode = "400",
        description = "Dates was invalid: start date was the same or later than end date or start date was in the past."
    )
    @ApiResponse(
        responseCode = "404",
        description = "User from jwt token was not found."
    )
    @ApiResponse(
        responseCode = "422",
        description = "Reservation overlaps with other reservation at requested term"
    )
    @PostMapping("/reservation")
    fun makeReservation(
        @RequestBody reservationDto: ReservationDto,
        request: HttpServletRequest
    ): ResponseEntity<*> = try {
        val token = request.getHeader(HttpHeaders.AUTHORIZATION).substringAfter(BEARER)
        val email = jwtTokenService.getUsernameFromToken(token)
        val userId = userService.findUserByEmail(email)?.id
            ?: throw UsernameNotFoundException("user with email: $email not found")
        val reservation = reservationDto.toDomain(userId)
        val savedReservation = reservationService.saveReservation(reservation)
        ResponseEntity.ok(savedReservation.toDto())
    } catch (e: UsernameNotFoundException) {
        ResponseEntity.notFound().build<Void>()
    } catch (e: IllegalArgumentException) {
        ResponseEntity.badRequest().body(e.message)
    } catch (e: ReservationException) {
        ResponseEntity.unprocessableEntity().body(e.message)
    }

    @Operation(
        summary = "Get reservations",
        description = "`filter` is optional. Possible values are: all, active, passed, cancelled. " +
                "If the `filter` is not provided then endpoint returns all user's reservations. "
    )
    @ApiResponse(
        responseCode = "200",
        description = "Successfully got reservation list. Note that `UserReservationDto` is encapsulated in `PageDto` " +
                "which has `data` - list of UserReservationDto and `last` field indicating whether the page was last or not",
        content = [
            Content(mediaType = "application/json", schema = Schema(oneOf = [UserReservationDto::class]))
        ]
    )
    @ApiResponse(
        responseCode = "400",
        description = "page or pageSize number was illegal or provided filter was not correct"
    )
    @ApiResponse(
        responseCode = "404",
        description = "User from jwt token was not found."
    )
    @ApiResponse(
        responseCode = "422",
        description = "Reserved flat was not found"
    )
    @GetMapping("/reservations")
    fun getReservations(
        @RequestParam page: Int,
        @RequestParam pageSize: Int,
        @RequestParam filter: String?,
        request: HttpServletRequest
    ): ResponseEntity<*> = try {
        val token = request.getHeader(HttpHeaders.AUTHORIZATION).substringAfter(BEARER)
        val email = jwtTokenService.getUsernameFromToken(token)
        val userId = userService.findUserByEmail(email)?.id
            ?: throw UsernameNotFoundException("user with email: $email not found")
        val reservationFilter = when (filter?.lowercase()) {
            null -> ReservationFilter.All
            "all" -> ReservationFilter.All
            "active" -> ReservationFilter.Active
            "passed" -> ReservationFilter.Passed
            "cancelled" -> ReservationFilter.Cancelled
            else -> throw IllegalArgumentException("Invalid reservationStatus. Possible values are: all, active, passed or cancelled")
        }
        val reservationPage = reservationService.getReservations(userId, page, pageSize, reservationFilter)
        val reservationsPageDto: PageDto<List<UserReservationDto>> = reservationPage.toDto { reservation ->
            with(reservation) {
                val flat = flatService.findById(flatId)
                    ?: throw FlatNotFoundException("flat with id: $flatId was not found")
                val price = flatPriceService.getPriceByFlatId(flatId, startDate, endDate)
                UserReservationDto(
                    flatId = flatId,
                    reservationId = id!!,
                    title = flat.title,
                    thumbnailUrl = flat.thumbnailUrl,
                    city = flat.address.city,
                    country = flat.address.country,
                    startDate = startDate.toString(),
                    endDate = endDate.toString(),
                    totalPrice = price
                )
            }
        }
        ResponseEntity.ok().body(reservationsPageDto)
    } catch (e: IllegalArgumentException) {
        ResponseEntity.badRequest().body(e.message)
    } catch (e: FlatNotFoundException) {
        ResponseEntity.unprocessableEntity().body(e.message)
    } catch (e: UsernameNotFoundException) {
        ResponseEntity.notFound().build<Void>()
    }

    @Operation(
        summary = "Get unavailable periods for reservations.",
        description = "Gets list of periods in which reservations for the flat, year and month are already present. " +
                "Note that dates are in `yyyy-mm-dd` format."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Successfully got unavailable periods.",
        content = [
            Content(mediaType = "application/json", schema = Schema(oneOf = [DatePeriodsDto::class]))
        ]
    )
    @ApiResponse(
        responseCode = "400",
        description = "Request params were wrong",
    )
    @GetMapping("/reservation/{flatId}/unavailable")
    fun getUnavailableDates(
        @PathVariable flatId: String,
        @RequestParam month: Int,
        @RequestParam year: Int,
    ): ResponseEntity<*> = try {
        require(month in 1..12) { "month number must be in [1; 12]" }
        require(year >= 1970) { "year must be greater or equal than 1970" }
        val reservedDates = reservationService.getUnavailableDates(flatId, month, year)
        val dto = DatePeriodsDto(reservedDates.map(LocalDateRange::toDto))
        ResponseEntity.ok(dto)
    } catch (e: IllegalArgumentException) {
        ResponseEntity.badRequest().body(e.message)
    }

    @Operation(
        summary = "Get reservation details",
        description = "dates are in `yyyy-mm-dd` format. Possible values of status are: `active`, `cancelled`, `passed`"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Successfully got reservation details.",
        content = [
            Content(mediaType = "application/json", schema = Schema(oneOf = [ReservationDetailsDto::class]))
        ]
    )
    @ApiResponse(
        responseCode = "404",
        description = "Reservation was not found."
    )
    @GetMapping("/reservation/{reservationId}")
    fun getReservationDetails(@PathVariable reservationId: Long): ResponseEntity<*> = try {
        val reservation = reservationService.getReservation(reservationId)
            ?: throw ReservationNotFoundException("Reservation with id $reservationId was not found.")
        val flat = flatService.findById(reservation.flatId) ?: throw FlatNotFoundException("Flat not found")
        val user = userService.findById(reservation.userId)
            ?: throw UsernameNotFoundException("User from reservation not found")
        val today = timeProvider().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val reservationStatus = when {
            reservation.cancelled -> "cancelled"
            reservation.endDate < today -> "passed"
            else -> "active"
        }
        val detailsDto = ReservationDetailsDto(
            reservationId = reservation.id!!,
            flatId = reservation.flatId,
            addressDto = flat.address.toDto(),
            startDate = reservation.startDate.toString(),
            endDate = reservation.endDate.toString(),
            phoneNumber = flat.owner.phoneNumber,
            email = flat.owner.email,
            clientName = user.name,
            clientLastName = user.lastName,
            bedrooms = flat.bedrooms,
            bathrooms = flat.bathrooms,
            beds = flat.beds,
            facilities = flat.facilities,
            adults = reservation.adults,
            children = reservation.children,
            pets = reservation.pets,
            price = flatPriceService.getPriceByFlatId(flat.id!!, reservation.startDate, reservation.endDate).toFloat(),
            status = reservationStatus,
            specialRequests = reservation.specialRequests
        )
        ResponseEntity.ok(detailsDto)
    } catch (e: ReservationNotFoundException) {
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
    } catch (e: FlatNotFoundException) {
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
    } catch (e: UsernameNotFoundException) {
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
    }

    companion object {
        private const val BEARER = "Bearer "
    }

}
