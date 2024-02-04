package pw.react.backend.controller

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.put
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import pw.react.backend.exceptions.UserValidationException
import pw.react.backend.security.common.AuthenticationService
import pw.react.backend.security.jwt.services.JwtTokenService
import pw.react.backend.services.UserService
import pw.react.backend.stubs.stubUser
import pw.react.backend.web.ChangeLastNameDto
import pw.react.backend.web.ChangeMailDto
import pw.react.backend.web.ChangeNameDto
import pw.react.backend.web.ChangePasswordDto
import pw.react.backend.web.UserDetailsDto

@WebMvcTest(controllers = [UserController::class])
@ContextConfiguration
@WebAppConfiguration
class UserControllerTest {

    @MockkBean
    private lateinit var userService: UserService

    @MockkBean
    private lateinit var jwtTokenService: JwtTokenService

    @MockkBean(relaxUnitFun = true)
    private lateinit var authenticationService: AuthenticationService

    @Autowired
    private lateinit var context: WebApplicationContext

    private lateinit var webMvc: MockMvc

    @BeforeEach
    fun setup() {
        webMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply<DefaultMockMvcBuilder?>(SecurityMockMvcConfigurers.springSecurity())
            .build()
        every { jwtTokenService.getUsernameFromToken("jwt") } returns "john.smith@mail.com"
        every { userService.findUserByEmail("john.smith@mail.com") } returns stubUser(id = 1)
    }

    @Test
    @WithMockUser
    fun `Returns user data`() {
        webMvc.get("/user/data") {
            header("Authorization", "Bearer jwt")
        }.andExpect {
            status { isOk() }
            content { json(Json.encodeToString(UserDetailsDto("John", "Smith", "john.smith@mail.com"))) }
        }
    }

    @Test
    @WithMockUser
    fun `Throws Unauthorized if authentication did not succeed`() {
        every { authenticationService.authenticate("john.smith@mail.com", "password321") } throws Exception("bad creds")
        webMvc.put("/user/password") {
            with(csrf())
            header("Authorization", "Bearer jwt")
            contentType = MediaType.APPLICATION_JSON
            content = Json.encodeToString(ChangePasswordDto("password321", "password123"))
        }.andExpect {
            status { isUnauthorized() }
        }
    }

    @Test
    @WithMockUser
    fun `Return ok if password was changed correctly`() {
        every { userService.updatePassword(stubUser(id = 1), "123password") } returns stubUser(
            id = 1,
            password = "123password"
        )
        webMvc.put("/user/password") {
            with(csrf())
            header("Authorization", "Bearer jwt")
            contentType = MediaType.APPLICATION_JSON
            content = Json.encodeToString(ChangePasswordDto("password123", "123password"))
        }.andExpect {
            status { isOk() }
            content { json(Json.encodeToString(UserDetailsDto("John", "Smith", "john.smith@mail.com"))) }
        }
    }

    @Test
    @WithMockUser
    fun `Return user data if name was changed`() {
        every { userService.updateName(stubUser(id = 1), "Andy") } returns stubUser(
            id = 1,
            name = "Andy"
        )
        webMvc.put("/user/name") {
            with(csrf())
            header("Authorization", "Bearer jwt")
            contentType = MediaType.APPLICATION_JSON
            content = Json.encodeToString(ChangeNameDto("Andy"))
        }.andExpect {
            status { isOk() }
            content { json(Json.encodeToString(UserDetailsDto("Andy", "Smith", "john.smith@mail.com"))) }
        }
    }

    @Test
    @WithMockUser
    fun `Return user data if last name was changed`() {
        every { userService.updateLastName(stubUser(id = 1), "Doberman") } returns stubUser(
            id = 1,
            lastName = "Doberman"
        )
        webMvc.put("/user/lastName") {
            with(csrf())
            header("Authorization", "Bearer jwt")
            contentType = MediaType.APPLICATION_JSON
            content = Json.encodeToString(ChangeLastNameDto("Doberman"))
        }.andExpect {
            status { isOk() }
            content { json(Json.encodeToString(UserDetailsDto("John", "Doberman", "john.smith@mail.com"))) }
        }
    }

    @Test
    @WithMockUser
    fun `Return user data if email was changed`() {
        every { userService.updateEmail(stubUser(id = 1), "john@mail.com") } returns stubUser(
            id = 1,
            email = "john@mail.com"
        )
        webMvc.put("/user/email") {
            with(csrf())
            header("Authorization", "Bearer jwt")
            contentType = MediaType.APPLICATION_JSON
            content = Json.encodeToString(ChangeMailDto("john@mail.com"))
        }.andExpect {
            status { isOk() }
            content { json(Json.encodeToString(UserDetailsDto("John", "Smith", "john@mail.com"))) }
        }
    }

    @Test
    @WithMockUser
    fun `Return bad request if new email was invalid`() {
        every {
            userService.updateEmail(stubUser(id = 1), "john@mail.com")
        } throws UserValidationException("Email invalid or taken")
        webMvc.put("/user/email") {
            with(csrf())
            header("Authorization", "Bearer jwt")
            contentType = MediaType.APPLICATION_JSON
            content = Json.encodeToString(ChangeMailDto("john@mail.com"))
        }.andExpect {
            status { isBadRequest() }
        }
    }

}
