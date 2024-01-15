package pw.react.backend.services

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import org.junit.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import pw.react.backend.dao.FlatEntityRepository
import pw.react.backend.stubFlat
import pw.react.backend.stubFlatEntity
import pw.react.backend.stubFlatQuery
import pw.react.backend.utils.TimeProvider


class FlatServiceTest {

    private val repository = mockk<FlatEntityRepository>().apply {
        every {
            findAll(match<PageRequest> { it.pageNumber == 0 && it.pageSize == 1 })
        } returns PageImpl(listOf(stubFlatEntity(id = "1")))
        every {
            findAll(match<PageRequest> { it.pageNumber == 1 && it.pageSize == 1 })
        } returns PageImpl(listOf(stubFlatEntity(id = "2")))
        every {
            findAll(any(), match<PageRequest> { it.pageNumber == 0 && it.pageSize == 1 })
        } returns PageImpl(listOf(stubFlatEntity(id = "3", bedrooms = 1)))
        every {
            findAll(any(), match<PageRequest> { it.pageNumber == 1 && it.pageSize == 1 })
        } returns PageImpl(listOf(stubFlatEntity(id = "4", bedrooms = 2)))
    }
    private val timeProvider = mockk<TimeProvider>().also {
        every { it.invoke() } returns Clock.System.now()
    }
    private val service = FlatService(repository, timeProvider)


    @Test
    fun `findAll returns correct pages`() {
        val page1 = service.findAll(PageRequest.of(0, 1))
        val page2 = service.findAll(PageRequest.of(1, 1))
        page1 shouldBe PageImpl(listOf(stubFlat(id = "1")))
        page2 shouldBe PageImpl(listOf(stubFlat(id = "2")))
    }

    @Test
    fun `findAll with query return correct pages`() {
        val page1 = service.findAll(stubFlatQuery(bedrooms = 1), PageRequest.of(0, 1))
        val page2 = service.findAll(stubFlatQuery(bedrooms = 2), PageRequest.of(1, 1))
        page1 shouldBe PageImpl(listOf(stubFlat(id = "3", bedrooms = 1)))
        page2 shouldBe PageImpl(listOf(stubFlat(id = "4", bedrooms = 2)))
    }

    @Test
    fun `findAll throws IllegalArgumentException when startDate is later than endDate`() {
        shouldThrow<IllegalArgumentException> {
            service.findAll(
                stubFlatQuery(
                    startDate = LocalDate(2019, 1, 1),
                    endDate = LocalDate(2018, 12, 1)
                ),
                PageRequest.of(0, 1)
            )
        }.message shouldBe "startDate must be earlier than endDate."
    }

    @Test
    fun `findAll throws IllegalArgumentException when endDate is in the past`() {
        shouldThrow<IllegalArgumentException> {
            service.findAll(
                stubFlatQuery(
                    startDate = LocalDate(2018, 12, 1),
                    endDate = LocalDate(2019, 1, 1)
                ),
                PageRequest.of(0, 1)
            )
        }.message shouldBe "endDate must be in the future."
    }

    @Test
    fun `findAll throws IllegalArgumentException when adults, children or pets are negative is in the past`() {
        val queries = listOf(
            stubFlatQuery(adults = -1),
            stubFlatQuery(children = -1),
            stubFlatQuery(pets = -1)
        )
        queries.forEach { query ->
            shouldThrow<IllegalArgumentException> {
                service.findAll(query, PageRequest.of(0, 1))
            }
        }
    }

    @Test
    fun `findAll throws IllegalArgumentException when there is 0 guests`() {
        shouldThrow<IllegalArgumentException> {
            service.findAll(stubFlatQuery(adults = 0, children = 0), PageRequest.of(0, 1))
        }.message shouldBe "There must be at least one guest: adult or children"
    }

    @Test
    fun `findAll throws IllegalArgumentException when beds, bedrooms or bathrooms number are negative`() {
        val queries = listOf(
            stubFlatQuery(beds = -1),
            stubFlatQuery(bedrooms = -1),
            stubFlatQuery(bathrooms = -1),
        )
        queries.forEach { query ->
            shouldThrow<IllegalArgumentException> {
                service.findAll(query, PageRequest.of(0, 1))
            }
        }
    }
}
