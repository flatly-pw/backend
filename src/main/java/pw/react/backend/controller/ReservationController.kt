package pw.react.backend.controller

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
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

    @PostMapping("/reservation")
    fun makeReservation(
        @RequestBody reservationDto: ReservationDto,
        request: HttpServletRequest
    ): ResponseEntity<*> {
        val token = request.getHeader(HttpHeaders.AUTHORIZATION).substringAfter(BEARER)
        val email = jwtTokenService.getUsernameFromToken(token)
        val userId = userService.findUserByEmail(email)?.id
            ?: throw UsernameNotFoundException("user with email: $email not found")
        val reservation = reservationDto.toDomain(userId)
        if (!reservationService.canReserveFlat(reservation.flatId, reservation.startDate, reservation.endDate)) {
            return ResponseEntity.unprocessableEntity()
                .body("Cannot make a reservation because the flat is already occupied at that term")
        }
        val savedReservation = reservationService.saveReservation(reservation)
        return ResponseEntity.ok(savedReservation.toDto())
    }

    companion object {
        private const val BEARER = "Bearer "
    }

}
