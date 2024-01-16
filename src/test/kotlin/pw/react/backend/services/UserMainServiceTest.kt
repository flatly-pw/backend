package pw.react.backend.services

import io.kotest.assertions.throwables.shouldThrow
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.password.PasswordEncoder
import pw.react.backend.dao.UserRepository
import pw.react.backend.exceptions.UserValidationException
import pw.react.backend.models.entity.UserEntity
import pw.react.backend.stubUser
import pw.react.backend.stubUserEntity
import java.util.*

class UserMainServiceTest {

    private val repository = mockk<UserRepository>()
    private val passwordEncoder = mockk<PasswordEncoder>().apply {
        every { encode("password123") } returns "encoded-password"
    }
    private val service = UserMainService(repository, passwordEncoder)


    @Test
    fun `saveUnique throws UserValidationException if email is empty`() {
        shouldThrow<UserValidationException> {
            service.saveUnique(stubUser(email = ""))
        }
    }

    @Test
    fun `saveUnique throws UserValidationException if email is invalid`() {
        val invalidMails = listOf("mail", "", "mail@", "@mail", "@mail.com", "@mail.", "@mail.c", "user@mail")
        val usersWithInvalidMails = invalidMails.map { stubUser(email = it) }
        usersWithInvalidMails.forEach { user ->
            shouldThrow<UserValidationException> {
                service.saveUnique(user)
            }
        }
    }

    @Test
    fun `saveUnique throws UserValidationException if name is empty`() {
        shouldThrow<UserValidationException> {
            service.saveUnique(stubUser(name = ""))
        }
    }

    @Test
    fun `saveUnique throws UserValidationException if name is blank`() {
        shouldThrow<UserValidationException> {
            service.saveUnique(stubUser(name = "    "))
        }
    }

    @Test
    fun `saveUnique throws UserValidationException if last name is empty`() {
        shouldThrow<UserValidationException> {
            service.saveUnique(stubUser(lastName = ""))
        }
    }

    @Test
    fun `saveUnique throws UserValidationException if last name is blank`() {
        shouldThrow<UserValidationException> {
            service.saveUnique(stubUser(lastName = "    "))
        }
    }

    @Test
    fun `saveUnique throws UserValidationException if password is shorter than MIN_PASSWORD_LENGTH`() {
        shouldThrow<UserValidationException> {
            service.saveUnique(stubUser(password = "pass"))
        }
    }

    @Test
    fun `saveUnique throws UserValidationException if password is longer than MAX_PASSWORD_LENGTH`() {
        shouldThrow<UserValidationException> {
            service.saveUnique(stubUser(password = "a".repeat(33)))
        }
    }

    @Test
    fun `saveUnique throws UserValidationException if user already exists`() {
        val user = stubUser()
        val userEntity = stubUserEntity()
        every { repository.findByEmail(user.email) } returns Optional.of(userEntity)
        shouldThrow<UserValidationException> {
            service.saveUnique(user)
        }
    }

    @Test
    fun `saveUnique calls password encoder`() {
        val user = stubUser(id = 1L)
        val savedUser = stubUserEntity(password = "encoded-password")
        every { repository.findByEmail(user.email) } returns Optional.empty()
        every {
            repository.saveAll(match<List<UserEntity>> { it.first() == savedUser })
        } returns listOf(savedUser)
        service.saveUnique(user)
        verify(exactly = 1) { passwordEncoder.encode("password123") }
    }

    @Test
    fun `saveUnique call repository saveAll with user with encoded password`() {
        val user = stubUser()
        val savedUser = stubUserEntity(password = "encoded-password")
        every { repository.findByEmail(user.email) } returns Optional.empty()
        every {
            repository.saveAll(match<List<UserEntity>> { it.first() == savedUser })
        } returns listOf(savedUser)
        service.saveUnique(user)
        verify(exactly = 1) {
            repository.saveAll(
                match<List<UserEntity>> {
                    it.first().email == savedUser.email && it.first().password == savedUser.password
                }
            )
        }
    }

}
