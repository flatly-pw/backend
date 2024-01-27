package pw.react.backend.services

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import pw.react.backend.dao.FlatEntityRepository
import pw.react.backend.exceptions.FlatImageException
import pw.react.backend.exceptions.FlatNotFoundException
import pw.react.backend.stubs.stubFlatDetails
import pw.react.backend.stubs.stubFlatEntity
import pw.react.backend.stubs.stubFlatReview
import java.util.*

class FlatDetailsServiceTest {

    private val flatEntityRepository = mockk<FlatEntityRepository>().also {
        every { it.findById("1") } returns Optional.of(stubFlatEntity(id = "1"))
        every { it.findById("non-existing-id") } returns Optional.empty()
    }
    private val flatReviewService = mockk<FlatReviewService>().also {
        every { it.getNumberOfReviewByFlatId("1") } returns 2
        every { it.getTopReviewsByFlatId("1") } returns listOf(
            stubFlatReview(reviewerName = "John"),
            stubFlatReview(reviewerName = "Anthony")
        )
        every { it.getRatingByFlatId("1") } returns 4.5f
    }
    private val flatPriceService = mockk<FlatPriceService>().also {
        every { it.getPriceByFlatId("1") } returns 20.0
    }
    private val flatImageService = mockk<FlatImageService>().also {
        every { it.getThumbnailUriByFlatId("1") } returns "image://flat/1"
        every { it.getImageUrisByFlatId("1") } returns listOf("image://flat/1", "image://flat/2")
    }
    private val service =
        FlatDetailsService(flatEntityRepository, flatReviewService, flatPriceService, flatImageService)

    @Test
    fun `Throws FlatNotFoundException if flat with given id was not fount`() {
        shouldThrow<FlatNotFoundException> {
            service.getFlatDetailsById(id = "non-existing-id")
        }.message shouldBe "Flat with id: non-existing-id was not found"
    }

    @Test
    fun `Throws ThumbnailNotFoundException if thumbnail for flat was not found`() {
        every { flatImageService.getThumbnailUriByFlatId("1") } throws FlatImageException.ThumbnailNotFound("")
        shouldThrow<FlatImageException.ThumbnailNotFound> { service.getFlatDetailsById("1") }
    }

    @Test
    fun `Returns correct details`() {
        val expectedDetails = stubFlatDetails(facilities = emptyList())
        service.getFlatDetailsById("1") shouldBe expectedDetails
    }
}
