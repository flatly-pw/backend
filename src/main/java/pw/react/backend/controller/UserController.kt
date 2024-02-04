package pw.react.backend.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pw.react.backend.exceptions.UserValidationException
import pw.react.backend.models.domain.User
import pw.react.backend.services.UserService
import pw.react.backend.web.UserDto
import pw.react.backend.web.toDto

@RestController
@RequestMapping(path = [UserController.USERS_PATH])
class UserController(private val userService: UserService) {
    @Operation(summary = "Create new users")
    @ApiResponse(
        responseCode = "201",
        description = "Users created",
        content = [
            Content(mediaType = "application/json", schema = Schema(allOf = [UserDto::class]))
        ]
    )
    @ApiResponse(responseCode = "401", description = "Something went wrong")
    @PostMapping("")
    fun createUsers(@RequestBody users: Collection<UserDto>): ResponseEntity<Collection<UserDto>> {
        return try {
            val newUsers = userService.batchSave(users.map(UserDto::toDomain)).map(User::toDto)
            log.info("Password is not going to be encoded")
            ResponseEntity.status(HttpStatus.CREATED).body(newUsers)
        } catch (ex: Exception) {
            throw UserValidationException(ex.message, USERS_PATH)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(UserController::class.java)
        const val USERS_PATH = "/users"
    }
}
