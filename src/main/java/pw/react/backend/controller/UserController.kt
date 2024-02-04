package pw.react.backend.controller

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pw.react.backend.security.common.AuthenticationService
import pw.react.backend.security.jwt.services.JwtTokenService
import pw.react.backend.services.UserService
import pw.react.backend.web.ChangePasswordDto

@RestController
@RequestMapping(UserController.USERS_PATH)
class UserController(
    private val userService: UserService,
    private val jwtTokenService: JwtTokenService,
    private val authenticationService: AuthenticationService,
) {

    @PutMapping("/password")
    fun changePassword(changePasswordDto: ChangePasswordDto, request: HttpServletRequest): ResponseEntity<*> = try {
        val token = request.getHeader(AUTHORIZATION).substringAfter(BEARER)
        val email = jwtTokenService.getUsernameFromToken(token)
        val user = userService.findUserByEmail(email)
            ?: throw UsernameNotFoundException("User with email: $email was not found")
        authenticationService.authenticate(user.email, changePasswordDto.currentPassword)
        userService.updatePassword(user, changePasswordDto.newPassword)
        ResponseEntity.ok().build<Void>()
    } catch (e: UsernameNotFoundException) {
        ResponseEntity.badRequest().body(e.message)
    } catch (e: Exception) {
        ResponseEntity.badRequest().body(e.message)
    }

    companion object {
        private val log = LoggerFactory.getLogger(UserController::class.java)
        const val USERS_PATH = "/users"
        const val AUTHORIZATION = "Authorization"
        const val BEARER = "Bearer "
    }
}
