package pw.react.backend.controller

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
        ResponseEntity.badRequest().body(e.message)
    } catch (e: IllegalArgumentException) {
        ResponseEntity.badRequest().body(e.message)
    } catch (e: ReservationException) {
        ResponseEntity.unprocessableEntity().body(e.message)
    }

    companion object {
        private const val BEARER = "Bearer "
    }

}
