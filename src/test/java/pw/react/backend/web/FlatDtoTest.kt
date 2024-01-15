package pw.react.backend.web

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.Test
import pw.react.backend.models.domain.Flat

class FlatDtoTest {

    @Test
    fun `Correctly maps Flat to FlatDto`() {
        val flat = Flat("This is example flat", 10, 1, 2, 3, "uuid")
        val flatDto = FlatDto("This is example flat", 10, 1, 2, 3, "uuid");
        flat.toDto() shouldBe flatDto
    }

    @Test
    fun `toDto throws NullPointerException when id in Flat is null`() {
        val flat = Flat("This is example flat", 10, 1, 2, 3, null)
        shouldThrow<NullPointerException> { flat.toDto() }
    }
}
