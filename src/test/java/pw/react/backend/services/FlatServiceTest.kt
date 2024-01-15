package pw.react.backend.services

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import pw.react.backend.dao.FlatEntityRepository
import pw.react.backend.stubFlat
import pw.react.backend.stubFlatEntity


class FlatServiceTest {

    private val repository = mockk<FlatEntityRepository>().apply {
        every {
            findAll(match<PageRequest> { it.pageNumber == 0 && it.pageSize == 1 })
        } returns PageImpl(listOf(stubFlatEntity(id = "1")))
        every {
            findAll(match<PageRequest> { it.pageNumber == 1 && it.pageSize == 1 })
        } returns PageImpl(listOf(stubFlatEntity(id = "2")))
    }
    private val service = FlatService(repository)


    @Test
    fun `findAll returns correct pages`() {
        val page1 = service.findAll(PageRequest.of(0, 1))
        val page2 = service.findAll(PageRequest.of(1, 1))
        page1 shouldBe PageImpl(listOf(stubFlat(id = "1")))
        page2 shouldBe PageImpl(listOf(stubFlat(id = "2")))
    }
}
