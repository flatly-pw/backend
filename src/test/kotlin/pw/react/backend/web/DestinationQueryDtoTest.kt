package pw.react.backend.web

import io.kotest.matchers.shouldBe
import org.junit.Test
import pw.react.backend.stubDestinationQuery
import pw.react.backend.stubDestinationQueryDto

class DestinationQueryDtoTest {

    @Test
    fun `Correctly maps Dto to Domain`()  {
        val dto = stubDestinationQueryDto()
        val domain = stubDestinationQuery()
        dto.toDomain() shouldBe domain
    }
}
