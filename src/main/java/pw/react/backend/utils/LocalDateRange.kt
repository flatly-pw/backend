package pw.react.backend.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toKotlinLocalDate
import pw.react.backend.utils.LocalDateRange.Companion.areDisjoint
import pw.react.backend.utils.LocalDateRange.Companion.join
import java.time.YearMonth
import java.time.LocalDate as JavaLocalDate

data class LocalDateRange(
    val start: LocalDate,
    val end: LocalDate
) {

    init {
        require(start < end) { "$start must be earlier than $end" }
    }

    fun isEmpty() = this == EMPTY

    operator fun contains(date: LocalDate): Boolean = start <= date && date <= end

    override fun toString(): String = "[$start - $end]"

    companion object {

        val EMPTY = LocalDateRange(JavaLocalDate.MIN.toKotlinLocalDate(), LocalDate(0, 1, 1))

        fun ofMonth(month: Int, year: Int): LocalDateRange {
            val yearMonth = YearMonth.of(year, month)
            return LocalDate(year, month, 1)..LocalDate(year, month, yearMonth.lengthOfMonth())
        }

        operator fun LocalDate.rangeTo(other: LocalDate): LocalDateRange {
            return LocalDateRange(this, other)
        }

        operator fun JavaLocalDate.rangeTo(other: JavaLocalDate): LocalDateRange {
            return LocalDateRange(this.toKotlinLocalDate(), other.toKotlinLocalDate())
        }

        fun areDisjoint(range1: LocalDateRange, range2: LocalDateRange) =
            range1.end < range2.start || range2.end < range1.start

        /**
         * Acts like a sum of sets but both [LocalDateRange] are required to have non-empty intersections. If one
         * of the ranges is *EMPTY* then the one that is not-empty will be returned.
         *
         * @throws IllegalArgumentException when both ranges are empty or when both are non-empty but disjoint.
         */
        infix fun LocalDateRange.join(other: LocalDateRange): LocalDateRange {
            when {
                isEmpty() && other.isEmpty() -> throw IllegalArgumentException("Cannot join two empty ranges")
                isEmpty() -> return other
                other.isEmpty() -> return this
            }

            if (areDisjoint(this, other)) throw IllegalArgumentException("Cannot join two disjoint LocalDateRange")
            val outStart = minOf(start, other.start)
            val outEnd = maxOf(end, other.end)
            return outStart..outEnd
        }

        infix fun LocalDateRange.intersect(other: LocalDateRange): LocalDateRange {
            if (isEmpty() || other.isEmpty()) return EMPTY
            if (areDisjoint(this, other)) return EMPTY
            val outStart = maxOf(start, other.start)
            val outEnd = minOf(end, other.end)
            return outStart..outEnd
        }
    }
}

/**
 * Simplifies list of [LocalDateRange] so overlapping ranges are joined into one range. The result of this operation
 * is list of disjoint [LocalDateRange]
 */
fun List<LocalDateRange>.joinOverlappingRanges(): List<LocalDateRange> =
    fold(initial = emptyList()) { processedReservations, current ->
        val last = processedReservations.lastOrNull() ?: return@fold listOf(current)
        if (!areDisjoint(last, current)) {
            val joined = last join current
            return@fold processedReservations.dropLast(1) + joined
        }
        processedReservations + current
    }
