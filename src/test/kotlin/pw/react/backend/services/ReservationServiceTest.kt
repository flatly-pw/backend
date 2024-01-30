package pw.react.backend.services

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import pw.react.backend.dao.FlatEntityRepository
import pw.react.backend.dao.ReservationRepository
import pw.react.backend.dao.UserRepository
import pw.react.backend.exceptions.ReservationCancellationException
import pw.react.backend.exceptions.ReservationException
import pw.react.backend.exceptions.ReservationNotFoundException
import pw.react.backend.models.domain.ReservationFilter
import pw.react.backend.models.entity.ReservationEntity
import pw.react.backend.stubs.stubFlatEntity
import pw.react.backend.stubs.stubReservation
import pw.react.backend.stubs.stubReservationEntity
import pw.react.backend.stubs.stubUserEntity
import pw.react.backend.utils.LocalDateRange.Companion.rangeTo
import pw.react.backend.utils.TimeProvider
import java.util.*

class ReservationServiceTest {

    private val reservationRepository = mockk<ReservationRepository>()
    private val userRepository = mockk<UserRepository>()
    private val flatRepository = mockk<FlatEntityRepository>()
    private val timeProvider = mockk<TimeProvider>()
    private val service = ReservationService(reservationRepository, userRepository, flatRepository, timeProvider)

    @BeforeEach
    fun setup() {
        every { reservationRepository.countAllActiveWithOverlappingDates("1", any(), any()) } returns 0
        every { reservationRepository.save(match<ReservationEntity> { it.flat.id == "1" }) } returns stubReservationEntity(
            user = stubUserEntity(),
            flat = stubFlatEntity(),
            id = 1L
        )
        every { userRepository.findById(1) } returns Optional.of(stubUserEntity())
        every { flatRepository.findById("1") } returns Optional.of(stubFlatEntity())
        every { timeProvider.invoke() } returns Instant.DISTANT_PAST
        every { reservationRepository.findAllByUserIdOrderByStartDateAsc(1, any()) } returns PageImpl(
            listOf(
                stubReservationEntity(stubUserEntity(1), stubFlatEntity("1")),
                stubReservationEntity(stubUserEntity(1), stubFlatEntity("2")),
                stubReservationEntity(stubUserEntity(1), stubFlatEntity("3"))
            )
        )
        every { reservationRepository.findAllActiveByUserId(1, any(), any()) } returns PageImpl(
            listOf(stubReservationEntity(stubUserEntity(1), stubFlatEntity("1")))
        )
        every { reservationRepository.findAllPassedByUserId(1, any(), any()) } returns PageImpl(
            listOf(stubReservationEntity(stubUserEntity(1), stubFlatEntity("2")))
        )
        every { reservationRepository.findAllCancelledByUserId(1, any()) } returns PageImpl(
            listOf(stubReservationEntity(stubUserEntity(1), stubFlatEntity("3")))
        )
    }

    @Test
    fun `Throws IllegalArgumentException if start date is after end date`() {
        val reservation = stubReservation(
            userId = 1,
            flatId = "1",
            startDate = LocalDate(2023, 1, 10),
            endDate = LocalDate(2023, 1, 1)
        )
        shouldThrow<IllegalArgumentException> {
            service.saveReservation(reservation)
        }
    }

    @Test
    fun `Throws IllegalArgumentException if start date is in the past`() {
        every { timeProvider.invoke() } returns LocalDate(2023, 1, 10).atStartOfDayIn(TimeZone.currentSystemDefault())
        val reservation = stubReservation(
            userId = 1,
            flatId = "1",
            startDate = LocalDate(2023, 1, 1),
            endDate = LocalDate(2023, 1, 11)
        )
        shouldThrow<IllegalArgumentException> {
            service.saveReservation(reservation)
        }
    }

    @Test
    fun `Throws ReservationException if provided reservation dates overlaps with other reservation for this flat`() {
        every { reservationRepository.countAllActiveWithOverlappingDates("1", any(), any()) } returns 1
        val reservation = stubReservation(1, "1")
        shouldThrow<ReservationException> {
            service.saveReservation(reservation)
        }
    }

    @Test
    fun `Saves reservation to repository if reservation data was correct`() {
        service.saveReservation(stubReservation(1, "1"))
        verify(exactly = 1) { reservationRepository.save(match<ReservationEntity> { it.flat.id == "1" }) }
    }

    @Test
    fun `Returns saved reservation`() {
        service.saveReservation(stubReservation(1, "1", id = null)) shouldBe stubReservation(1, "1", id = 1L)
    }

    @Test
    fun `Throws IllegalArgumentException when page or pageSize are incorrect`() {
        shouldThrow<IllegalArgumentException> { service.getReservations(1, -1, 10) }
        shouldThrow<IllegalArgumentException> { service.getReservations(1, 0, 0) }
        shouldThrow<IllegalArgumentException> { service.getReservations(1, 0, -1) }
    }

    @Test
    fun `Returns all reservations if filter is All`() {
        val expected = PageImpl(
            listOf(
                stubReservation(1, "1"),
                stubReservation(1, "2"),
                stubReservation(1, "3")
            )
        )
        service.getReservations(1, 0, 10, ReservationFilter.All) shouldBe expected
    }

    @Test
    fun `Returns active reservations if filter is Active`() {
        val expected = PageImpl(
            listOf(stubReservation(1, "1"))
        )
        service.getReservations(1, 0, 10, ReservationFilter.Active) shouldBe expected
    }

    @Test
    fun `Returns passed reservations if filter is Passed`() {
        val expected = PageImpl(
            listOf(stubReservation(1, "2"))
        )
        service.getReservations(1, 0, 10, ReservationFilter.Passed) shouldBe expected
    }

    @Test
    fun `Returns cancelled reservations if filter is Cancelled`() {
        val expected = PageImpl(
            listOf(stubReservation(1, "3"))
        )
        service.getReservations(1, 0, 10, ReservationFilter.Cancelled) shouldBe expected
    }

    @Test
    fun `Returns reservations periods for reservations that start and end in specified month and year`() {
        every { reservationRepository.getReservationsByFlatIdAndMonthYear("1", 2, 2024) } returns listOf(
            stubReservationEntity(
                stubUserEntity(id = 1),
                stubFlatEntity("1"),
                startDate = date(12, 2),
                endDate = date(15, 2)
            ),
            stubReservationEntity(
                stubUserEntity(id = 2),
                stubFlatEntity("1"),
                startDate = date(24, 2),
                endDate = date(26, 2)
            ),
            stubReservationEntity(
                stubUserEntity(id = 3),
                stubFlatEntity("1"),
                startDate = date(26, 2),
                endDate = date(29, 2)
            )
        )
        val expectedPeriods = listOf(
            date(12, 2)..date(15, 2),
            date(24, 2)..date(29, 2)
        )
        service.getUnavailableDates(flatId = "1", month = 2, year = 2024) shouldBe expectedPeriods
    }

    @Test
    fun `Returns reservation periods trimmed to requested month for reservations that start or end in different months than specified`() {
        every { reservationRepository.getReservationsByFlatIdAndMonthYear("1", 2, 2024) } returns listOf(
            stubReservationEntity(
                stubUserEntity(id = 1),
                stubFlatEntity("1"),
                startDate = date(28, 1),
                endDate = date(4, 2)
            ),
            stubReservationEntity(
                stubUserEntity(id = 2),
                stubFlatEntity("1"),
                startDate = date(26, 2),
                endDate = date(3, 3)
            ),
        )
        // 1.02.2024 to 29.02.2024 are the correct dates for february
        val expectedPeriods = listOf(
            date(1, 2)..date(4, 2),
            date(26, 2)..date(29, 2)
        )
        service.getUnavailableDates("1", 2, 2024) shouldBe expectedPeriods
    }

    @Test
    fun `Returns whole month if reservation period starts and ends in different month than specified`() {
        every { reservationRepository.getReservationsByFlatIdAndMonthYear("1", 2, 2024) } returns listOf(
            stubReservationEntity(
                stubUserEntity(id = 1),
                stubFlatEntity("1"),
                startDate = date(20, 1),
                endDate = date(4, 3)
            ),
        )
        // reservation starts in January and ends in March. Specified month is February so the whole February is unavailable
        val expectedPeriods = listOf(
            date(1, 2)..date(29, 2)
        )
        service.getUnavailableDates("1", 2, 2024) shouldBe expectedPeriods
    }

    @Test
    fun `Throws ReservationNotFound exception if reservation was not found`() {
        every { timeProvider() } returns Instant.DISTANT_FUTURE
        every { reservationRepository.findById(1) } returns Optional.empty()
        shouldThrow<ReservationNotFoundException> {
            service.cancelReservation(1, 1)
        }
    }

    @Test
    fun `Throws IllegalArgumentException if userId is different tha userId from reservation`() {
        every { timeProvider() } returns Instant.DISTANT_FUTURE
        every { reservationRepository.findById(1) } returns Optional.of(
            stubReservationEntity(
                user = stubUserEntity(id = 2),
                flat = stubFlatEntity()
            )
        )
        shouldThrow<IllegalArgumentException> {
            service.cancelReservation(1, userId = 1)
        }
    }

    @Test
    fun `Throws ReservationCancellationException if trying to cancel after reservation endDate`() {
        every { timeProvider() } returns Instant.DISTANT_FUTURE
        every { reservationRepository.findById(1) } returns Optional.of(
            stubReservationEntity(
                user = stubUserEntity(id = 2),
                flat = stubFlatEntity()
            )
        )
        shouldThrow<ReservationCancellationException> {
            service.cancelReservation(1, userId = 2)
        }
    }

    @Test
    fun `Throws ReservationCancellationException if trying to cancel after is started`() {
        every { timeProvider() } returns LocalDate(2023, 1, 5).atStartOfDayIn(TimeZone.currentSystemDefault())
        every { reservationRepository.findById(1) } returns Optional.of(
            stubReservationEntity(
                user = stubUserEntity(id = 2),
                flat = stubFlatEntity()
            )
        )
        shouldThrow<ReservationCancellationException> {
            service.cancelReservation(1, userId = 2)
        }
    }

    @Test
    fun `Throws ReservationCancellationException if trying to cancel already cancelled reservation`() {
        every { timeProvider() } returns Instant.DISTANT_PAST
        every { reservationRepository.findById(1) } returns Optional.of(
            stubReservationEntity(
                user = stubUserEntity(id = 2),
                flat = stubFlatEntity(),
                cancelled = true
            )
        )
        shouldThrow<ReservationCancellationException> {
            service.cancelReservation(1, userId = 2)
        }
    }

    @Test
    fun `Returns cancelled reservation`() {
        every { timeProvider() } returns Instant.DISTANT_PAST
        every { reservationRepository.findById(1) } returns Optional.of(
            stubReservationEntity(
                id = 1,
                user = stubUserEntity(id = 2),
                flat = stubFlatEntity()
            )
        )
        every { reservationRepository.save(match<ReservationEntity> { it.id == 1L && it.cancelled }) } returns stubReservationEntity(
            id = 1,
            user = stubUserEntity(id = 2),
            flat = stubFlatEntity(),
            cancelled = true
        )
        val expected = stubReservation(id = 1, userId = 2, flatId = "1", cancelled = true)
        service.cancelReservation(1, 2) shouldBe expected
    }

    private fun date(d: Int, m: Int, y: Int = 2024) = LocalDate(y, m, d)
}
