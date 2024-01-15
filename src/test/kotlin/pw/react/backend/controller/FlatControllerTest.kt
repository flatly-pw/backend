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
import pw.react.backend.models.domain.Flat
import pw.react.backend.services.FlatService
import pw.react.backend.stubFlat
import pw.react.backend.web.toDto
import kotlin.math.exp

@WebMvcTest(controllers = [FlatController::class])
@ContextConfiguration
@WebAppConfiguration
class FlatControllerTest {

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
    }

    @Test
    @WithMockUser
    fun `Responds with BadRequest if page is less than to 0`() {
        webMvc.get("/flats?page=-1&pageSize=10").andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    @WithMockUser
    fun `Responds with BadRequest if pageSize is less or equal to 0`() {
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
    fun `Responds with PageDto if page and page size are correct and data was found`() {
        val page = PageImpl(listOf(stubFlat(id = "1"), stubFlat(id = "2")))
        every {
            flatService.findAll(match { it.pageNumber == 0 && it.pageSize == 5 })
        } returns page
        val expectedDto = page.toDto(Flat::toDto)
        webMvc.get("/flats") {
            param("page", "0")
            param("pageSize", "5")
        }.andExpect {
            content {
                json(Json.encodeToString(expectedDto))
            }
        }
    }
}
