package pw.react.backend.controller

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pw.react.backend.exceptions.UserValidationException
import pw.react.backend.models.domain.User
import pw.react.backend.security.common.AuthenticationService
import pw.react.backend.security.jwt.services.JwtTokenService
import pw.react.backend.services.UserService
import pw.react.backend.web.ChangeMailDto
import pw.react.backend.web.ChangeNameDto
import pw.react.backend.web.ChangePasswordDto
import pw.react.backend.web.toDetailsDto

@RestController
@RequestMapping(UserController.USERS_PATH)
class UserController(
    private val userService: UserService,
    private val jwtTokenService: JwtTokenService,
    private val authenticationService: AuthenticationService,
) {

    @PutMapping("/password")
    fun changePassword(
        @RequestBody changePasswordDto: ChangePasswordDto,
        request: HttpServletRequest
    ): ResponseEntity<*> = try {
        val token = request.getHeader(AUTHORIZATION).substringAfter(BEARER)
        val email = jwtTokenService.getUsernameFromToken(token)
        val user = userService.findUserByEmail(email)
            ?: throw UsernameNotFoundException("User with email: $email was not found")
        authenticationService.authenticate(user.email, changePasswordDto.currentPassword)
        val updatedUser = userService.updatePassword(user, changePasswordDto.newPassword)
        ResponseEntity.ok(updatedUser.toDetailsDto())
    } catch (e: UsernameNotFoundException) {
        ResponseEntity.badRequest().body(e.message)
    } catch (e: IllegalArgumentException) {
        ResponseEntity.badRequest().body(e.message)
    }

    @PutMapping("/name")
    fun changeNameAndLastName(
        @RequestBody changeNameDto: ChangeNameDto,
        request: HttpServletRequest
    ): ResponseEntity<*> = try {
        val token = request.getHeader(AUTHORIZATION).substringAfter(BEARER)
        val email = jwtTokenService.getUsernameFromToken(token)
        val user = userService.findUserByEmail(email)
            ?: throw UsernameNotFoundException("User with email: $email was not found")
        require(changeNameDto.newName != null || changeNameDto.newLastName != null) {
            "At least one of newName or lastName has to be not null"
        }
        var updatedUser: User? = null
        changeNameDto.newName?.let { newName ->
            updatedUser = userService.updateName(user, newName)
        }
        changeNameDto.newLastName?.let { newLastName ->
            updatedUser = userService.updateLastName(user, newLastName)
        }
        ResponseEntity.ok(updatedUser!!.toDetailsDto())
    } catch (e: UsernameNotFoundException) {
        ResponseEntity.badRequest().body(e.message)
    } catch (e: IllegalArgumentException) {
        ResponseEntity.badRequest().body(e.message)
    } catch (e: UserValidationException) {
        ResponseEntity.badRequest().body(e.message)
    }

    @PutMapping("/email")
    fun changeMail(
        @RequestBody changeMailDto: ChangeMailDto,
        request: HttpServletRequest
    ): ResponseEntity<*> = try {
        val token = request.getHeader(AUTHORIZATION).substringAfter(BEARER)
        val email = jwtTokenService.getUsernameFromToken(token)
        val user = userService.findUserByEmail(email)
            ?: throw UsernameNotFoundException("User with email: $email was not found")
        val updatedUser = userService.updateEmail(user, changeMailDto.newMail)
        ResponseEntity.ok(updatedUser.toDetailsDto())
    } catch (e: UsernameNotFoundException) {
        ResponseEntity.badRequest().body(e.message)
    } catch (e: UserValidationException) {
        ResponseEntity.badRequest().body(e.message)
    }

    companion object {
        private val log = LoggerFactory.getLogger(UserController::class.java)
        const val USERS_PATH = "/users"
        const val AUTHORIZATION = "Authorization"
        const val BEARER = "Bearer "
    }
}
