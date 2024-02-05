package pw.react.backend.controller

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
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
import org.springframework.test.web.servlet.put
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import pw.react.backend.exceptions.ReservationCancellationException
import pw.react.backend.exceptions.ReservationException
import pw.react.backend.exceptions.ReservationNotFoundException
import pw.react.backend.models.domain.ReservationFilter
import pw.react.backend.security.jwt.services.JwtTokenService
import pw.react.backend.services.FlatPriceService
import pw.react.backend.services.FlatService
import pw.react.backend.services.ReservationService
import pw.react.backend.services.UserService
import pw.react.backend.stubs.stubAddress
import pw.react.backend.stubs.stubFlat
import pw.react.backend.stubs.stubFlatOwner
import pw.react.backend.stubs.stubReservation
import pw.react.backend.stubs.stubReservationDto
import pw.react.backend.stubs.stubUser
import pw.react.backend.utils.TimeProvider
import pw.react.backend.web.PageDto
import pw.react.backend.web.ReservationDetailsDto
import pw.react.backend.web.ReservationDto
import pw.react.backend.web.UserReservationDto
import pw.react.backend.web.toDto

@WebMvcTest(controllers = [ReservationController::class])
@ContextConfiguration
@WebAppConfiguration
class ReservationControllerTest {

    @MockkBean
    private lateinit var flatService: FlatService

    @MockkBean
    private lateinit var reservationService: ReservationService

    @MockkBean
    private lateinit var flatPriceService: FlatPriceService

    @MockkBean
    private lateinit var jwtTokenService: JwtTokenService

    @MockkBean
    private lateinit var userService: UserService

    @MockkBean
    private lateinit var timeProvider: TimeProvider

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

    @Test
    @WithMockUser
    fun `Returns correct reservations no matter the case of filter`() {
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
        webMvc.getReservations(page = 0, pageSize = 10, "all").andExpect {
            content { json(Json.encodeToString(expectedDto)) }
        }
        webMvc.getReservations(page = 0, pageSize = 10, "All").andExpect {
            content { json(Json.encodeToString(expectedDto)) }
        }
        webMvc.getReservations(page = 0, pageSize = 10, "ALL").andExpect {
            content { json(Json.encodeToString(expectedDto)) }
        }
        webMvc.getReservations(page = 0, pageSize = 10, "aLl").andExpect {
            content { json(Json.encodeToString(expectedDto)) }
        }
    }

    @Test
    @WithMockUser
    fun `Returns NotFound when reservation was not found`() {
        every { reservationService.getReservation(1) } returns null
        webMvc.get("/reservation/1").andExpect {
            status { isNotFound() }
        }
    }

    @Test
    @WithMockUser
    fun `Returns NotFound when flat from reservation was not found`() {
        every { reservationService.getReservation(1) } returns stubReservation(userId = 1, flatId = "1")
        every { flatService.findById("1") } returns null
        webMvc.get("/reservation/1").andExpect {
            status { isNotFound() }
        }
    }

    @Test
    @WithMockUser
    fun `Returns NotFound when user from reservation was not found`() {
        every { reservationService.getReservation(1) } returns stubReservation(userId = 1, flatId = "1")
        every { flatService.findById("1") } returns stubFlat(id = "1")
        every { userService.findById(1) } returns null
        webMvc.get("/reservation/1").andExpect {
            status { isNotFound() }
        }
    }

    @Test
    @WithMockUser
    fun `Returns correct ReservationDetailsDto`() {
        every { reservationService.getReservation(1) } returns stubReservation(id = 1, userId = 1, flatId = "1")
        every { flatService.findById("1") } returns stubFlat(id = "1")
        every { userService.findById(1) } returns stubUser(id = 1)
        every { timeProvider() } returns LocalDate(2022, 12, 12).atStartOfDayIn(TimeZone.currentSystemDefault())
        every { flatPriceService.getPriceByFlatId("1", LocalDate(2023, 1, 1), LocalDate(2023, 1, 10)) } returns 800.0
        val expectedDto = ReservationDetailsDto(
            reservationId = 1,
            flatId = "1",
            addressDto = stubAddress().toDto(),
            startDate = "2023-01-01",
            endDate = "2023-01-10",
            owner = stubFlatOwner().toDto(),
            clientName = "John",
            clientLastName = "Smith",
            bedrooms = 1,
            bathrooms = 2,
            beds = 1,
            facilities = listOf("wi-fi"),
            adults = 2,
            children = 1,
            pets = 0,
            price = 800f,
            specialRequests = "Pink pillow",
            status = "active"
        )
        webMvc.get("/reservation/1").andExpect {
            status { isOk() }
            content { json(Json.encodeToString(expectedDto)) }
        }
    }

    @Test
    @WithMockUser
    fun `Return NotFound when user was not found`() {
        every { userService.findUserByEmail("john.smith@mail.com") } returns null
        webMvc.cancelReservation(1).andExpect {
            status { isNotFound() }
        }
    }

    @Test
    @WithMockUser
    fun `Return NotFound if reservation was not found`() {
        every { reservationService.cancelReservation(1, 1) } throws ReservationNotFoundException()
        webMvc.cancelReservation(1).andExpect {
            status { isNotFound() }
        }
    }

    @Test
    @WithMockUser
    fun `Return Unauthorized if reservation was not placed by calling user`() {
        every { reservationService.cancelReservation(1, 1) } throws IllegalArgumentException()
        webMvc.cancelReservation(1).andExpect {
            status { isUnauthorized() }
        }
    }

    @Test
    @WithMockUser
    fun `Return BadRequest if reservation could not be cancelled`() {
        every { reservationService.cancelReservation(1, 1) } throws ReservationCancellationException()
        webMvc.cancelReservation(1).andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    @WithMockUser
    fun `Return cancelled reservation`() {
        every { reservationService.cancelReservation(1, 1) } returns stubReservation(
            id = 1,
            userId = 1,
            flatId = "1",
            cancelled = true
        )
        val expectedDto = stubReservationDto(flatId = "1")
        webMvc.cancelReservation(1).andExpect {
            status { isOk() }
            content { json(Json.encodeToString(expectedDto)) }
        }
    }

    @Test
    @WithMockUser
    fun `Return saved reservation with external user id`() {
        every {
            reservationService.saveReservation(stubReservation(userId = 1, flatId = "1", externalUserId = 123L))
        } returns stubReservation(userId = 1, flatId = "1", externalUserId = 123L)
        val expectedDto = stubReservationDto(flatId = "1")
        webMvc.postReservation(stubReservationDto(flatId = "1"), externalUserId = 123L)
            .andExpect {
                content { json(Json.encodeToString(expectedDto)) }
            }
        verify { reservationService.saveReservation(stubReservation(userId = 1, flatId = "1", externalUserId = 123L)) }
    }

    private fun MockMvc.postReservation(dto: ReservationDto? = null, externalUserId: Long? = null) =
        post("/reservation") {
            with(csrf())
            header("Authorization", "Bearer jwt")
            externalUserId?.let {
                param("externalUserId", "$externalUserId")
            }
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

    private fun MockMvc.cancelReservation(reservationId: Long) = put("/reservation/cancel/$reservationId") {
        with(csrf())
        header("Authorization", "Bearer jwt")
    }
}
