package pw.react.backend.web

import io.kotest.matchers.shouldBe
import org.junit.Test
import pw.react.backend.stubFlatQuery
import pw.react.backend.stubFlatQueryDto

class FlatQueryDtoTest {

    @Test
    fun `Correctly maps dto to domain`() {
        val dto = stubFlatQueryDto()
        val domain = stubFlatQuery()
        dto.toDomain() shouldBe domain
    }
}
