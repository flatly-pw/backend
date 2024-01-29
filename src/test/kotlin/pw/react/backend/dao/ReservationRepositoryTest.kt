package pw.react.backend.dao

import io.kotest.matchers.collections.shouldContainOnly
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

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
        val expectedReservationIds = listOf<Long>(10)
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

}
