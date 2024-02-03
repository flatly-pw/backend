package pw.react.backend.services

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import pw.react.backend.dao.FlatEntityRepository
import pw.react.backend.stubs.stubFlat
import pw.react.backend.stubs.stubFlatEntity
import pw.react.backend.stubs.stubFlatQuery


class FlatServiceTest {

    private val repository = mockk<FlatEntityRepository>().apply {
        every {
            findAll(any(), match<PageRequest> { it.pageNumber == 0 && it.pageSize == 1 })
        } returns PageImpl(listOf(stubFlatEntity(id = "1")))
        every {
            findAll(any(), match<PageRequest> { it.pageNumber == 1 && it.pageSize == 1 })
        } returns PageImpl(listOf(stubFlatEntity(id = "2")))
    }
    private val imageService = mockk<FlatImageService>().also {
        every { it.getThumbnailUriByFlatId("1") } returns "image://flat/1"
        every { it.getThumbnailUriByFlatId("2") } returns "image://flat/2"
    }
    private val reviewService = mockk<FlatReviewService>().also {
        every { it.getRatingByFlatId("1") } returns 4.7f
        every { it.getRatingByFlatId("2") } returns 5.0f
    }
    private val priceService = mockk<FlatPriceService>().also {
        every { it.getPriceByFlatId("1") } returns 100.0
        every { it.getPriceByFlatId("2") } returns 250.0
    }

    private val facilityService = mockk<FlatFacilityService>()

    private val service = FlatService(repository, imageService, reviewService, priceService, facilityService)

    @Test
    fun `findAll returns correct pages`() {
        val page1 = service.findAll(stubFlatQuery(page = 0, pageSize = 1))
        val page2 = service.findAll(stubFlatQuery(page = 1, pageSize = 1))
        page1 shouldBe PageImpl(listOf(stubFlat(id = "1", pricePerNight = 100.0, rating = 4.7f)))
        page2 shouldBe PageImpl(listOf(stubFlat(id = "2", pricePerNight = 250.0, rating = 5.0f)))
    }

    @Test
    fun `Throws IllegalArgumentException when page is less than 0`() {
        shouldThrow<IllegalArgumentException> {
            service.findAll(stubFlatQuery(page = -1, pageSize = 1))
        }
    }

    @Test
    fun `Throws IllegalArgumentException when page size is less than 0`() {
        shouldThrow<IllegalArgumentException> {
            service.findAll(stubFlatQuery(page = 0, pageSize = -1))
        }
    }

    @Test
    fun `Throws IllegalArgumentException when page size is 0`() {
        shouldThrow<IllegalArgumentException> {
            service.findAll(stubFlatQuery(page = 0, pageSize = 0))
        }
    }
}
