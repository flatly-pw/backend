package pw.react.backend.controller

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.domain.PageImpl
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import pw.react.backend.exceptions.FlatImageException
import pw.react.backend.exceptions.FlatNotFoundException
import pw.react.backend.models.FlatQueryFactory
import pw.react.backend.models.domain.Flat
import pw.react.backend.services.FlatDetailsService
import pw.react.backend.services.FlatImageService
import pw.react.backend.services.FlatService
import pw.react.backend.stubs.stubFlat
import pw.react.backend.stubs.stubFlatDetails
import pw.react.backend.stubs.stubFlatDetailsDto
import pw.react.backend.stubs.stubFlatImage
import pw.react.backend.stubs.stubFlatQuery
import pw.react.backend.web.toDto

@WebMvcTest(controllers = [FlatController::class])
@ContextConfiguration
@WebAppConfiguration
class FlatControllerTest {

    @MockkBean
    private lateinit var flatService: FlatService

    @MockkBean
    private lateinit var flatDetailsService: FlatDetailsService

    @MockkBean
    private lateinit var flatImageService: FlatImageService

    @MockkBean
    private lateinit var flatQueryFactory: FlatQueryFactory

    @Autowired
    private lateinit var context: WebApplicationContext

    private lateinit var webMvc: MockMvc

    @BeforeEach
    fun setup() {
        webMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply<DefaultMockMvcBuilder?>(SecurityMockMvcConfigurers.springSecurity())
            .build()
    }

    @Test
    @WithMockUser
    fun `Responds with BadRequest if page is less than to 0`() {
        every {
            flatQueryFactory.create(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())
        } throws IllegalArgumentException()
        webMvc.get("/flats?page=-1&pageSize=10").andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    @WithMockUser
    fun `Responds with BadRequest if pageSize is less or equal to 0`() {
        every {
            flatQueryFactory.create(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())
        } throws IllegalArgumentException()
        webMvc.get("/flats?page=0&pageSize=0").andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    @WithMockUser
    fun `Responds with BadRequest if page or pageSize query params are missing`() {
        webMvc.get("/flats").andExpect {
            status { isBadRequest() }
        }
        webMvc.get("/flats?page=0").andExpect {
            status { isBadRequest() }
        }
        webMvc.get("/flats?pageSize=10").andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    @WithMockUser
    fun `Responds with PageDto if page and page size are correct`() {
        val page = PageImpl(listOf(stubFlat(id = "1"), stubFlat(id = "2")))
        every {
            flatService.findAll(stubFlatQuery(page = 0, pageSize = 5))
        } returns page
        every {
            flatQueryFactory.create(
                0,
                5,
                "Warsaw",
                "Poland",
                "2030-10-01",
                "2020-10-11",
                3,
                2,
                1,
                1,
                2,
                0
            )
        } returns stubFlatQuery(page = 0, pageSize = 5)
        val expectedDto = page.toDto(Flat::toDto)
        webMvc.get("/flats") {
            param("page", "0")
            param("pageSize", "5")
            param("city", "Warsaw")
            param("country", "Poland")
            param("startDate", "2030-10-01")
            param("endDate", "2020-10-11")
            param("beds", "3")
            param("bedrooms", "2")
            param("bathrooms", "1")
            param("adults", "1")
            param("children", "2")
            param("pets", "0")
        }.andExpect {
            content {
                json(Json.encodeToString(expectedDto))
            }
        }
    }

    @Test
    @WithMockUser
    fun `Responds with BadRequest if FlatQueryDto contain invalid data`() {
        every {
            flatQueryFactory.create(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())
        } throws IllegalArgumentException()
        webMvc.get("/flats") {
            param("page", "0")
            param("pageSize", "5")
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    @WithMockUser
    fun `Responds with NotFound if Flat with given id was not found`() {
        every { flatDetailsService.getFlatDetailsById("1") } throws FlatNotFoundException("")
        webMvc.get("/flats/1").andExpect {
            status { isNotFound() }
        }
    }

    @Test
    @WithMockUser
    fun `Responds with FlatDetails for flat with matching id`() {
        every { flatDetailsService.getFlatDetailsById("1") } returns stubFlatDetails()
        val expectedDto = stubFlatDetailsDto()
        webMvc.get("/flats/1").andExpect {
            content { json(Json.encodeToString(expectedDto)) }
        }
    }

    @Test
    @WithMockUser
    fun `Responds with NotFound if Flat thumbnail was not found`() {
        every { flatDetailsService.getFlatDetailsById("1") } throws FlatImageException.ThumbnailNotFound("")
        webMvc.get("/flats/1").andExpect {
            status { isNotFound() }
        }
    }

    @Test
    @WithMockUser
    fun `Responds with NotFound if image was not found`() {
        every { flatImageService.getImage(any(), any()) } throws FlatImageException.ImageNotFound("")
        webMvc.get("/flats/1/image/1").andExpect {
            status { isNotFound() }
        }
    }

    @Test
    @WithMockUser
    fun `Responds with image bytes`() {
        every { flatImageService.getImage("1", "1") } returns stubFlatImage()
        webMvc.get("/flats/1/image/1").andExpect {
            content {
                bytes(stubFlatImage().bytes)
            }
            status { isOk() }
        }
    }
}
