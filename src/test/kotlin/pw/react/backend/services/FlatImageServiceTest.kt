package pw.react.backend.services

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import pw.react.backend.dao.FlatImageRepository
import pw.react.backend.exceptions.FlatImageException
import pw.react.backend.stubs.stubFlatEntity
import pw.react.backend.stubs.stubFlatImageEntity

class FlatImageServiceTest {

    private val imageEntity = stubFlatImageEntity(stubFlatEntity(id = "2"), id = "1")
    private val repository = mockk<FlatImageRepository>().also {
        every { it.findFlatImageBy("1", "2") } returns imageEntity
        every { it.findFlatImageBy("2", "2") } returns null
        every { it.findThumbnailByFlatId("1") } returns stubFlatImageEntity(stubFlatEntity(id = "1"))
        every { it.findThumbnailByFlatId("2") } returns null
        every { it.findFlatImageEntitiesByFlatId("2") } returns emptyList()
    }
    private val service = FlatImageService(repository)

    @Test
    fun `Throws ImageNotFound exception if repository returned null`() {
        shouldThrow<FlatImageException.ImageNotFound> {
            service.getImage("2", "2")
        }
    }

    @Test
    fun `Returns FlatImage`() {
        val image = service.getImage("1", "2")
        image.bytes shouldBeEqual imageEntity.bytes
    }

    @Test
    fun `Returns null if thumbnail was not found`() {
        service.getThumbnailUriByFlatId("2") shouldBe null
    }

    @Test
    fun `Returns empty list if images were not found`() {
        service.getImageUrisByFlatId("2").shouldBeEmpty()
    }
}
