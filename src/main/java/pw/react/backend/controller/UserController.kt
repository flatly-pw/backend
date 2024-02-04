package pw.react.backend.controller

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pw.react.backend.services.UserService

@RestController
@RequestMapping(UserController.USERS_PATH)
class UserController(private val userService: UserService) {



    companion object {
        private val log = LoggerFactory.getLogger(UserController::class.java)
        const val USERS_PATH = "/users"
    }
}
