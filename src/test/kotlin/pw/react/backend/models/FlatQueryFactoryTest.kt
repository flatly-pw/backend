package pw.react.backend.models

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.datetime.Instant
import org.junit.Test
import pw.react.backend.utils.TimeProvider

class FlatQueryFactoryTest {

    private val timeProvider = mockk<TimeProvider>().also {
        every { it.invoke() } returns Instant.DISTANT_PAST
    }
    private val factory = FlatQueryFactory(timeProvider)

    @Test
    fun `Throws IllegalArgumentException when page number is negative`() {
        shouldThrow<IllegalArgumentException> {
            factory.create(page = -1, pageSize = 5)
        }.message shouldBe "page must not be less than 0"
    }

    @Test
    fun `Throws IllegalArgumentException when pageSize is not positive`() {
        shouldThrow<IllegalArgumentException> {
            factory.create(page = 0, pageSize = -1)
        }.message shouldBe "page size must be greater than 0"
    }

    @Test
    fun `Throws IllegalArgumentException when startDate or endDate are not iso date`() {
        shouldThrow<IllegalArgumentException> {
            factory.create(page = 0, pageSize = 1, startDateIso = "2020.01.01", endDateIso = "2020-01-02")
        }
        shouldThrow<IllegalArgumentException> {
            factory.create(page = 0, pageSize = 1, startDateIso = "2020-01-01", endDateIso = "2020.01.02")
        }
    }

    @Test
    fun `Throws IllegalArgumentException when city is empty or blank`() {
        shouldThrow<IllegalArgumentException> {
            factory.create(page = 0, pageSize = 1, city = "")
        }
        shouldThrow<IllegalArgumentException> {
            factory.create(page = 0, pageSize = 1, city = "    ")
        }
    }

    @Test
    fun `Throws IllegalArgumentException when only one of startDate or endDate is supplied`() {
        shouldThrow<IllegalArgumentException> {
            factory.create(0, 1, startDateIso = "2020-01-01")
        }.message shouldBe "Both startDate and endDate must be either provided or not"
        shouldThrow<IllegalArgumentException> {
            factory.create(0, 1, endDateIso = "2020-01-01")
        }.message shouldBe "Both startDate and endDate must be either provided or not"
    }

    @Test
    fun `Throws IllegalArgumentException when statDate is later than endDate`() {
        shouldThrow<IllegalArgumentException> {
            factory.create(0, 1, startDateIso = "2020-01-01", endDateIso = "2019-01-01")
        }.message shouldBe "startDate must be earlier than endDate."
    }

    @Test
    fun `Throws IllegalArgumentException when startDate is in the past`() {
        every { timeProvider() } returns Instant.DISTANT_FUTURE
        shouldThrow<IllegalArgumentException> {
            factory.create(0, 1, startDateIso = "2020-01-01", endDateIso = "2020-01-02")
        }.message shouldBe "startDate must be in the future."
    }

    @Test
    fun `Throws IllegalArgumentException when pets are negative`() {
        shouldThrow<IllegalArgumentException> {
            factory.create(0, 1, pets = -1)
        }.message shouldBe "pets must not be less than 0"
    }

    @Test
    fun `Throws IllegalArgumentException when adults are negative`() {
        shouldThrow<IllegalArgumentException> {
            factory.create(0, 1, adults = 0)
        }.message shouldBe "Adults number must be positive"
    }

    @Test
    fun `Throws IllegalArgumentException when children are not positive`() {
        shouldThrow<IllegalArgumentException> {
            factory.create(0, 1, children = 0)
        }.message shouldBe "Children number must be positive"
    }

    @Test
    fun `Throws IllegalArgumentException when adults are negative and children positive`() {
        shouldThrow<IllegalArgumentException> {
            factory.create(0, 1, adults = -1, children = 1)
        }.message shouldBe "Adults number must not be negative"
    }

    @Test
    fun `Throws IllegalArgumentException when adults are positive and children are negative`() {
        shouldThrow<IllegalArgumentException> {
            factory.create(0, 1, adults = 1, children = -1)
        }.message shouldBe "Children number must not be negative"
    }

    @Test
    fun `Throws IllegalArgumentException when number of guests is 0`() {
        shouldThrow<IllegalArgumentException> {
            factory.create(0, 1, adults = 0, children = 0)
        }.message shouldBe "There must be at least one guest: adult or children"
    }

    @Test
    fun `Throws IllegalArgumentException when beds are negative`() {
        shouldThrow<IllegalArgumentException> {
            factory.create(0, 1, beds = -1)
        }.message shouldBe "beds number cannot be less than 0"
    }

    @Test
    fun `Throws IllegalArgumentException when bedrooms are negative`() {
        shouldThrow<IllegalArgumentException> {
            factory.create(0, 1, bedrooms = -1)
        }.message shouldBe "bedrooms number cannot be less than 0"
    }

    @Test
    fun `Throws IllegalArgumentException when bathrooms are negative`() {
        shouldThrow<IllegalArgumentException> {
            factory.create(0, 1, bathrooms = -1)
        }.message shouldBe "bathrooms number cannot be less than 0"
    }
}
