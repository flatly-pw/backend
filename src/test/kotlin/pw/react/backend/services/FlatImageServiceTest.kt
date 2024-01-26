package pw.react.backend.services

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.equals.shouldBeEqual
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import pw.react.backend.dao.FlatImageRepository
import pw.react.backend.exceptions.FlatImageException
import pw.react.backend.stubFlatEntity
import pw.react.backend.stubs.stubFlatImageEntity

class FlatImageServiceTest {

    private val imageEntity = stubFlatImageEntity(stubFlatEntity(id = "2"), id = "1")
    private val repository = mockk<FlatImageRepository>().also {
        every { it.findFlatImageBy("1", "2") } returns imageEntity
        every { it.findFlatImageBy("2", "2") } returns null
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
}
