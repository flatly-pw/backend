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
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import pw.react.backend.exceptions.ReservationException
import pw.react.backend.security.jwt.services.JwtTokenService
import pw.react.backend.services.ReservationService
import pw.react.backend.services.UserService
import pw.react.backend.stubs.stubReservation
import pw.react.backend.stubs.stubReservationDto
import pw.react.backend.stubs.stubUser
import pw.react.backend.web.ReservationDto

@WebMvcTest(controllers = [ReservationController::class])
@ContextConfiguration
@WebAppConfiguration
class ReservationControllerTest {

    @MockkBean
    private lateinit var reservationService: ReservationService

    @MockkBean
    private lateinit var jwtTokenService: JwtTokenService

    @MockkBean
    private lateinit var userService: UserService

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
        every { reservationService.saveReservation(stubReservation(1, "1")) } returns stubReservation(1, "1", id = 1)
    }

    @Test
    @WithMockUser
    fun `Responds with NotFound if user was not found`() {
        every { userService.findUserByEmail("john.smith@mail.com") } returns null
        webMvc.postReservation(stubReservationDto("1")).andExpect {
            status { isNotFound() }
        }
    }

    @Test
    @WithMockUser
    fun `Responds with BadRequest if reservation dto contained invalid data`() {
        every {
            reservationService.saveReservation(
                stubReservation(
                    userId = 1,
                    flatId = "1"
                )
            )
        } throws IllegalArgumentException()
        webMvc.postReservation(stubReservationDto("1")).andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    @WithMockUser
    fun `Responds with UnprocessableEntity if reservation term overlapped already placed reservation`() {
        every { reservationService.saveReservation(stubReservation(1, "1")) } throws ReservationException("")
        webMvc.postReservation(stubReservationDto("1")).andExpect {
            status { isUnprocessableEntity() }
        }
    }

    @Test
    @WithMockUser
    fun `Responds with reservation that was placed`() {
        webMvc.postReservation(stubReservationDto("1")).andExpect {
            content {
                json(Json.encodeToString(stubReservationDto("1")))
            }
        }
    }

    private fun MockMvc.postReservation(dto: ReservationDto? = null) = post("/reservation") {
        with(csrf())
        header("Authorization", "Bearer jwt")
        dto?.let {
            contentType = MediaType.APPLICATION_JSON
            content = Json.encodeToString(dto)
        }
    }
}
