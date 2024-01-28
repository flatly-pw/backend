package pw.react.backend.controller

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import kotlinx.datetime.LocalDate
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.domain.PageImpl
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import pw.react.backend.exceptions.ReservationException
import pw.react.backend.models.domain.ReservationFilter
import pw.react.backend.security.jwt.services.JwtTokenService
import pw.react.backend.services.FlatPriceService
import pw.react.backend.services.FlatService
import pw.react.backend.services.ReservationService
import pw.react.backend.services.UserService
import pw.react.backend.stubs.stubFlat
import pw.react.backend.stubs.stubReservation
import pw.react.backend.stubs.stubReservationDto
import pw.react.backend.stubs.stubUser
import pw.react.backend.web.PageDto
import pw.react.backend.web.ReservationDto
import pw.react.backend.web.UserReservationDto

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

    @MockkBean
    private lateinit var flatPriceService: FlatPriceService

    @MockkBean
    private lateinit var flatService: FlatService

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

    @Test
    @WithMockUser
    fun `Responds with NotFound if user from token was not found`() {
        every { userService.findUserByEmail(any()) } returns null
        webMvc.getReservations(page = 0, pageSize = 10)
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    @WithMockUser
    fun `Responds with BadRequest if filter is not valid`() {
        webMvc.getReservations(page = 0, pageSize = 10, filter = "invalid-filter")
            .andExpect {
                status { isBadRequest() }
            }
    }

    @Test
    @WithMockUser
    fun `Responds with UnprocessableEntity if flat from reservation was not found`() {
        every { reservationService.getReservations(1, 0, 10, ReservationFilter.All) } returns PageImpl(
            listOf(
                stubReservation(1, flatId = "1"),
                stubReservation(1, flatId = "2"),
            )
        )
        every { flatService.findById("1") } returns null
        webMvc.getReservations(page = 0, pageSize = 10)
            .andExpect {
                status { isUnprocessableEntity() }
            }
    }

    @Test
    @WithMockUser
    fun `Returns correct reservations`() {
        every { reservationService.getReservations(1, 0, 10, ReservationFilter.All) } returns PageImpl(
            listOf(stubReservation(1, flatId = "1", id = 123))
        )
        every { flatService.findById("1") } returns stubFlat(id = "1")
        every { flatPriceService.getPriceByFlatId("1", LocalDate(2023, 1, 1), LocalDate(2023, 1, 10)) } returns 800.0
        val expectedDto = PageDto(
            data = listOf(
                UserReservationDto(
                    flatId = "1",
                    reservationId = 123,
                    title = "Flat 1",
                    thumbnailUrl = "image://flat/1",
                    city = "Warsaw",
                    country = "Poland",
                    startDate = "2023-01-01",
                    endDate = "2023-01-10",
                    totalPrice = 800.0
                )
            ),
            last = true
        )
        webMvc.getReservations(page = 0, pageSize = 10)
            .andExpect {
                content { json(Json.encodeToString(expectedDto)) }
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

    private fun MockMvc.getReservations(page: Int, pageSize: Int, filter: String? = null) =
        get("/reservations") {
            header("Authorization", "Bearer jwt")
            param("page", page.toString())
            param("pageSize", pageSize.toString())
            filter?.let { param("filter", it) }
        }
}
