package pw.react.backend.dao

import io.kotest.matchers.collections.shouldContainOnly
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.Pageable

/**
 * Data for this test is provided by *data.sql* from resources folder.
 */
@DataJpaTest
class ReservationRepositoryTest {

    @Autowired
    private lateinit var reservationRepository: ReservationRepository

    @Test
    fun `Should return reservation that ends or starts in given month and year`() {
        val expectedReservationIds = listOf<Long>(6, 7)
        val actual = reservationRepository.getReservationsByFlatIdAndMonthYear("1", 2, 2025)
        val actualIds = actual.map { it.id }
        actualIds shouldContainOnly expectedReservationIds
    }

    @Test
    fun `Should return reservations that starts and ends in the same month and year`() {
        val expectedReservationIds = listOf<Long>(2, 3)
        val actual = reservationRepository.getReservationsByFlatIdAndMonthYear("2", 1, 2025)
        val actualIds = actual.map { it.id }
        actualIds shouldContainOnly expectedReservationIds
    }

    @Test
    fun `Should return reservation that starts in month and year before target and end in month after and the same year as target`() {
        val expectedReservationIds = listOf<Long>(8)
        val actual = reservationRepository.getReservationsByFlatIdAndMonthYear("1", 1, 2026)
        val actualIds = actual.map { it.id }
        actualIds shouldContainOnly expectedReservationIds
    }

    @Test
    fun `Should return reservations that start in month before target and in the same year and end month after target and in next year`() {
        val expectedReservationIds = listOf<Long>(9)
        val actual = reservationRepository.getReservationsByFlatIdAndMonthYear("2", 12, 2025)
        val actualIds = actual.map { it.id }
        actualIds shouldContainOnly expectedReservationIds
    }

    @Test
    fun `Should return reservations that start in month before target and end in month after target`() {
        val expectedReservationIds = listOf<Long>(10, 13)
        val actual = reservationRepository.getReservationsByFlatIdAndMonthYear("2", 4, 2026)
        val actualIds = actual.map { it.id }
        actualIds shouldContainOnly expectedReservationIds
    }

    @Test
    fun `Should return reservations that start in years before and end in years after target`() {
        val expectedReservationIds = listOf<Long>(11)
        val months = 1..12
        months.forEach { month ->
            val actual = reservationRepository.getReservationsByFlatIdAndMonthYear("2", month, 2028)
            val actualIds = actual.map { it.id }
            actualIds shouldContainOnly expectedReservationIds
        }
    }

    @Test
    fun `Should not return cancelled reservations`() {
        val expectedNonCancelledReservations = listOf<Long>(10, 13)
        val actual = reservationRepository.getReservationsByFlatIdAndMonthYear("2", 3, 2026)
        val actualIds = actual.map { it.id }
        actualIds shouldContainOnly expectedNonCancelledReservations
    }

    @Test
    fun `Should return all external user reservations`() {
        val expectedAllReservations = listOf<Long>(13, 14, 15, 16)
        val actual = reservationRepository.findAllExternalByUserId(1000, externalUserId = 123L, Pageable.ofSize(10))
        val actualIds = actual.map { it.id }
        actualIds shouldContainOnly expectedAllReservations
    }

    @Test
    fun `Should return active external user reservations`() {
        val expectedActiveReservations = listOf<Long>(15, 16)
        val actual = reservationRepository.findAllExternalActiveByUserId(
            1000,
            LocalDate(2026, 6, 1).toJavaLocalDate(), externalUserId = 123L,
            Pageable.ofSize(10)
        )
        val actualIds = actual.map { it.id }
        actualIds shouldContainOnly expectedActiveReservations
    }

    @Test
    fun `Should return passed external user reservations`() {
        val expectedPassedReservations = listOf<Long>(13)
        val actual = reservationRepository.findAllExternalPassedByUserId(
            1000,
            LocalDate(2026, 6, 1).toJavaLocalDate(), externalUserId = 123L,
            Pageable.ofSize(10)
        )
        val actualIds = actual.map { it.id }
        actualIds shouldContainOnly expectedPassedReservations
    }

    @Test
    fun `Should return cancelled external user reservations`() {
        val expectedCancelledReservations = listOf<Long>(14)
        val actual = reservationRepository.findAllExternalCancelledByUserId(
            1000,
            externalUserId = 123L,
            Pageable.ofSize(10)
        )
        val actualIds = actual.map { it.id }
        actualIds shouldContainOnly expectedCancelledReservations
    }

    @Test
    fun `Should return all reservations from normal user`() {
        val expected = listOf<Long>(6, 7, 8, 9, 10, 11, 12)
        val actual =
            reservationRepository.findAllByUserIdAndExternalUserIdIsNullOrderByStartDateAsc(1003L, Pageable.ofSize(120))
        val actualIds = actual.map { it.id }
        actualIds shouldContainOnly expected
    }

    @Test
    fun `Should return all active reservation from normal user`() {
        val expected = listOf<Long>(8, 9, 10, 11)
        val actual = reservationRepository.findAllActiveByUserId(
            1003,
            LocalDate(2026, 1, 1).toJavaLocalDate(),
            Pageable.ofSize(20)
        ).map { it.id }
        actual shouldContainOnly expected
    }

    @Test
    fun `Should return passed reservation from normal user`() {
        val expected = listOf<Long>(6, 7)
        val actual = reservationRepository.findAllPassedByUserId(
            1003L,
            LocalDate(2026, 1, 1).toJavaLocalDate(),
            Pageable.ofSize(20)
        ).map { it.id }
        actual shouldContainOnly expected
    }

    @Test
    fun `Should return cancelled reservation from normal user`() {
        val expected = listOf<Long>(12)
        val actual = reservationRepository.findAllCancelledByUserId(1003, Pageable.ofSize(20)).map { it.id }
        actual shouldContainOnly expected

    }
}
