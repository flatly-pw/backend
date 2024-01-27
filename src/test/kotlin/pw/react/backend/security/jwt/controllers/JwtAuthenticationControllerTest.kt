package pw.react.backend.security.jwt.controllers

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import pw.react.backend.exceptions.UnauthorizedException
import pw.react.backend.exceptions.UserValidationException
import pw.react.backend.security.common.AuthenticationService
import pw.react.backend.security.common.CommonUserDetailsService
import pw.react.backend.security.jwt.services.JwtTokenService
import pw.react.backend.services.UserService
import pw.react.backend.stubs.stubUserDto
import pw.react.backend.stubs.stubUserEntity

@WebMvcTest(controllers = [JwtAuthenticationController::class])
@ContextConfiguration
@WebAppConfiguration
@ActiveProfiles("jwt")
class JwtAuthenticationControllerTest {

    @MockkBean(relaxed = true)
    private lateinit var authenticationService: AuthenticationService

    @MockkBean
    private lateinit var jwtTokenService: JwtTokenService

    @MockkBean
    private lateinit var userDetailsService: CommonUserDetailsService

    @MockkBean(relaxed = true)
    private lateinit var userService: UserService

    @Autowired
    private lateinit var context: WebApplicationContext

    private lateinit var webMvc: MockMvc

    @BeforeEach
    fun setup() {
        webMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .build()
    }

    @Test
    fun `Responds with BadRequest if User already exists`() {
        val userDto = stubUserDto()
        every { userService.saveUnique(any()) } throws UserValidationException("User already exists")
        webMvc.post("/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = Json.encodeToString(userDto)
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `Responds with Unauthorized if authentication failed`() {
        val userDto = stubUserDto()
        every {
            authenticationService.authenticate(userDto.email, userDto.password)
        } throws UnauthorizedException("unauthorized", "")
        webMvc.post("/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = Json.encodeToString(userDto)
        }.andExpect {
            status { isUnauthorized() }
        }
    }

    @Test
    fun `Responds with JwtToken if register succeeded`() {
        val userDto = stubUserDto()
        every { userDetailsService.loadUserByUsername(userDto.email) } returns stubUserEntity() as UserDetails
        every {
            jwtTokenService.generateToken(match<UserDetails> { it.username == userDto.email }, any())
        } returns "123token123"
        webMvc.post("/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = Json.encodeToString(userDto)
        }.andExpect {
            content { string("{\"jwttoken\":\"123token123\"}") }
        }
    }
}
