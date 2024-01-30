package pw.react.backend.services

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.datetime.LocalDate
import org.junit.jupiter.api.Test
import pw.react.backend.dao.FlatPriceRepository
import pw.react.backend.stubs.stubPriceEntity

class FlatPriceServiceTest {

    private val flatPriceRepository = mockk<FlatPriceRepository>().also {
        every { it.getPriceEntityByFlatId("1") } returns stubPriceEntity(priceDollars = 100.0)
    }
    private val flatPriceService = FlatPriceService(flatPriceRepository)

    @Test
    fun `Return correct price for nights between start and end dates`() {
        val priceFor8Nights = flatPriceService.getPriceByFlatId("1", LocalDate(2023, 1, 1), LocalDate(2023, 1, 10))
        priceFor8Nights shouldBe 800.0
    }
}
