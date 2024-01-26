package pw.react.backend.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import pw.react.backend.exceptions.ReservationException
import pw.react.backend.security.jwt.services.JwtTokenService
import pw.react.backend.services.ReservationService
import pw.react.backend.services.UserService
import pw.react.backend.web.ReservationDto
import pw.react.backend.web.toDomain
import pw.react.backend.web.toDto

@RestController
class ReservationController(
    private val reservationService: ReservationService,
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

    companion object {
        private const val BEARER = "Bearer "
    }

}
