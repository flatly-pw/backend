package pw.react.backend.web

import io.kotest.matchers.shouldBe
import org.junit.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

class PageDtoTest {

    @Test
    fun `toDto correctly maps data with provided mapper lambda`() {
        val intPage = PageImpl((1..10).toList(), Pageable.ofSize(10), 11)
        val expectedDto = PageDto((1..10).map { it * it }, isLast = false)
        intPage.toDto { it * it } shouldBe expectedDto
    }
}
