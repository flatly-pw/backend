package pw.react.backend.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pw.react.backend.exceptions.FlatNotFoundException
import pw.react.backend.exceptions.ReservationException
import pw.react.backend.models.domain.ReservationFilter
import pw.react.backend.security.jwt.services.JwtTokenService
import pw.react.backend.services.FlatPriceService
import pw.react.backend.services.FlatService
import pw.react.backend.services.ReservationService
import pw.react.backend.services.UserService
import pw.react.backend.web.PageDto
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
    }

    companion object {
        private const val BEARER = "Bearer "
    }

}
