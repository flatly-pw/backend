package pw.react.backend.utils

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import kotlinx.datetime.LocalDate
import org.junit.jupiter.api.Test
import pw.react.backend.utils.LocalDateRange.Companion.EMPTY
import pw.react.backend.utils.LocalDateRange.Companion.intersect
import pw.react.backend.utils.LocalDateRange.Companion.join
import pw.react.backend.utils.LocalDateRange.Companion.rangeTo

class LocalDateRangeTest {

    @Test
    fun `Throws IllegalArgumentException on init when start is greater than end`() {
        val start = LocalDate(2024, 1, 10)
        val end = LocalDate(2024, 1, 9)
        shouldThrow<IllegalArgumentException> { start..end }
    }

    @Test
    fun `isEmpty returns true for EMPTY range`() {
        val empty = EMPTY
        empty.isEmpty().shouldBeTrue()
    }

    @Test
    fun `isEmpty returns false for non-empty range`() {
        val range = LocalDate(2024, 1, 1)..LocalDate(2024, 1, 3)
        range.isEmpty().shouldBeFalse()
    }

    @Test
    fun `contains should return true if given date is in range`() {
        val range = LocalDate(2024, 1, 2)..LocalDate(2024, 1, 4)
        (LocalDate(2024, 1, 1) in range).shouldBeFalse()
        (LocalDate(2024, 1, 2) in range).shouldBeTrue()
        (LocalDate(2024, 1, 3) in range).shouldBeTrue()
        (LocalDate(2024, 1, 4) in range).shouldBeTrue()
        (LocalDate(2024, 1, 5) in range).shouldBeFalse()
    }

    @Test
    fun `ofMonth returns correct range representing given month`() {
        val leapYearFebruaryRange = LocalDateRange.ofMonth(2, 2024)
        val normalYearFebruaryRange = LocalDateRange.ofMonth(2, 2023)

        leapYearFebruaryRange shouldBe LocalDate(2024, 2, 1)..LocalDate(2024, 2, 29)
        normalYearFebruaryRange shouldBe LocalDate(2023, 2, 1)..LocalDate(2023, 2, 28)
    }

    @Test
    fun `areDisjoint returns true if ranges are disjoint`() {
        val range1 = LocalDate(2024, 1, 2)..LocalDate(2024, 1, 4)
        val range2 = LocalDate(2024, 1, 5)..LocalDate(2024, 1, 8)
        LocalDateRange.areDisjoint(range1, range2).shouldBeTrue()
    }

    @Test
    fun `areDisjoint returns false if ranges are disjoint`() {
        val range1 = LocalDate(2024, 1, 1)..LocalDate(2024, 1, 5)
        val range2 = LocalDate(2024, 1, 5)..LocalDate(2024, 1, 8)
        LocalDateRange.areDisjoint(range1, range2).shouldBeFalse()
    }

    @Test
    fun `join returns IllegalArgumentException if both ranges are empty`() {
        shouldThrow<IllegalArgumentException> {
            EMPTY join EMPTY
        }
    }

    @Test
    fun `join returns non empty range if one them was empty`() {
        val range = LocalDate(2024, 1, 2)..LocalDate(2024, 1, 4)
        (range join EMPTY) shouldBe range
        (EMPTY join range) shouldBe range
    }

    @Test
    fun `join throws IllegalArgumentException if two ranges are disjoint`() {
        val range1 = LocalDate(2024, 1, 2)..LocalDate(2024, 1, 4)
        val range2 = LocalDate(2024, 1, 5)..LocalDate(2024, 1, 8)
        shouldThrow<IllegalArgumentException> {
            range1 join range2
        }
    }

    @Test
    fun `join returns correct range`() {
        val range1 = LocalDate(2024, 1, 2)..LocalDate(2024, 1, 4)
        val range2 = LocalDate(2024, 1, 4)..LocalDate(2024, 1, 8)
        val range3 = LocalDate(2024, 1, 1)..LocalDate(2024, 1, 6)
        val expected = range1.start..range2.end
        range1 join range2 shouldBe expected
        range1 join range1 shouldBe range1
        range2 join range3 shouldBe range3.start..range2.end
        range1 join range3 shouldBe range3
    }

    @Test
    fun `intersect returns EMPTY if one of ranges was empty`() {
        val range = LocalDate(2024, 1, 2)..LocalDate(2024, 1, 4)
        range intersect EMPTY shouldBe EMPTY
        EMPTY intersect range shouldBe EMPTY
        EMPTY intersect EMPTY shouldBe EMPTY
    }

    @Test
    fun `intersect returns EMPTY if ranges are disjoint`() {
        val range1 = LocalDate(2024, 1, 1)..LocalDate(2024, 1, 4)
        val range2 = LocalDate(2024, 1, 5)..LocalDate(2024, 1, 8)
        range1 intersect range2 shouldBe EMPTY
    }

    @Test
    fun `intersect returns correct range`() {
        val range1 = LocalDate(2024, 1, 1)..LocalDate(2024, 1, 7)
        val range2 = LocalDate(2024, 1, 5)..LocalDate(2024, 1, 8)
        val expectedRange = LocalDate(2024, 1, 5)..LocalDate(2024, 1, 7)
        range1 intersect range2 shouldBe expectedRange
        range2 intersect range1 shouldBe expectedRange
    }

    @Test
    fun `joinOverlappingRanges return list of joined ranges`() {
        val ranges = listOf(
            date(26, 11)..date(4, 12),
            date(12, 12)..date(15, 12),
            date(13, 12)..date(17, 12),
            date(18, 12)..date(21, 12),
            date(24, 12)..date(26, 12),
            date(26, 12)..date(27, 12),
            date(29, 12)..date(31, 12),
            date(31, 12)..date(2, 2, 2024)
        )
        val joinedRanges = listOf(
            date(26, 11)..date(4, 12),
            date(12, 12)..date(17, 12),
            date(18, 12)..date(21, 12),
            date(24, 12)..date(27, 12),
            date(29, 12)..date(2, 2, 2024),
        )
        ranges.joinOverlappingRanges() shouldBe joinedRanges
    }

    private fun date(d: Int, m: Int, y: Int = 2023) = LocalDate(y, m, d)
}
