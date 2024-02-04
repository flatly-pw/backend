package pw.react.backend.services

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.password.PasswordEncoder
import pw.react.backend.dao.UserRepository
import pw.react.backend.exceptions.UserValidationException
import pw.react.backend.models.entity.UserEntity
import pw.react.backend.stubs.stubUser
import pw.react.backend.stubs.stubUserEntity
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
        val user = stubUser()
        val savedUser = stubUserEntity(password = "encoded-password")
        every { repository.findByEmail(user.email) } returns Optional.empty()
        every {
            repository.saveAll(listOf(savedUser.apply { id = null }))
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
            repository.saveAll(listOf(savedUser.apply { id = null }))
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

    @Test
    fun `updateName throws UserValidation exception if name is empty or blank`() {
        shouldThrow<UserValidationException> {
            service.updateName(stubUser(id = 1), "")
            service.updateName(stubUser(id = 1), "  ")
        }
    }

    @Test
    fun `updateName throws UserValidation exception if user id is null`() {
        shouldThrow<UserValidationException> {
            service.updateName(stubUser(id = null), "Adam")
        }
    }

    @Test
    fun `updateName returns updated user`() {
        val user = stubUser(id = 1)
        every { repository.save(match<UserEntity> { it.id == 1L && it.name == "Adam" }) } returns stubUserEntity(
            id = 1,
            name = "Adam"
        )
        service.updateName(user, "Adam") shouldBe stubUser(id = 1, name = "Adam")
    }

    @Test
    fun `updateLastName throws UserValidation exception if last name is empty or blank`() {
        shouldThrow<UserValidationException> {
            service.updateLastName(stubUser(id = 1), "")
            service.updateName(stubUser(id = 1), "  ")
        }
    }

    @Test
    fun `updateLastName throws UserValidation exception if user id is null`() {
        shouldThrow<UserValidationException> {
            service.updateLastName(stubUser(id = null), "Adam")
        }
    }

    @Test
    fun `updateLastName return user with updated last name`() {
        val user = stubUser(id = 1)
        every { repository.save(match<UserEntity> { it.id == 1L && it.lastName == "Doberman" }) } returns stubUserEntity(
            id = 1,
            lastName = "Doberman"
        )
        service.updateLastName(user, "Doberman") shouldBe stubUser(id = 1, lastName = "Doberman")
    }

    @Test
    fun `updateEmail throws UserValidation exception if email is not valid`() {
        shouldThrow<UserValidationException> {
            service.updateEmail(stubUser(id = 1), "mail")
        }
    }

    @Test
    fun `updateEmail throws UserValidation exception if email is taken`() {
        every {
            repository.findByEmail("adam@mail.com")
        } returns Optional.of(stubUserEntity(id = 1, email = "adam@mail.com"))
        shouldThrow<UserValidationException> {
            service.updateEmail(stubUser(id = 1), "adam@mail.com")
        }
    }

    @Test
    fun `updateEmail throws UserValidation exception if id was null`() {
        every {
            repository.findByEmail("adam@mail.com")
        } returns Optional.empty()
        shouldThrow<UserValidationException> {
            service.updateEmail(stubUser(id = null), "adam@mail.com")
        }
    }

    @Test
    fun `updateEmail returns user with updated mail`() {
        every {
            repository.findByEmail("adam@mail.com")
        } returns Optional.empty()
        every {
            repository.save(match<UserEntity> { it.id == 1L && it.email == "adam@mail.com" })
        } returns stubUserEntity(id = 1, email = "adam@mail.com")
        service.updateEmail(stubUser(id = 1), "adam@mail.com") shouldBe stubUser(id = 1, email = "adam@mail.com")
    }

    @Test
    fun `updatePassword throws UserValidation exception if password is invalid`() {
        shouldThrow<UserValidationException> {
            service.updatePassword(stubUser(id = 1), "halo")
            service.updatePassword(stubUser(id = 1), "1".repeat(33))
        }
    }

    @Test
    fun `updatePassword throws UserValidation exception if user id is null`() {
        shouldThrow<UserValidationException> {
            service.updatePassword(stubUser(id = null), "halohalo")
        }
    }

    @Test
    fun `updatePassword returns user with updated password`() {
        every { passwordEncoder.encode("newpassword") } returns "encoded-password"
        every {
            repository.save(match<UserEntity> { it.id == 1L && it.password == "encoded-password" })
        } returns stubUserEntity(id = 1, password = "encoded-password")
        service.updatePassword(stubUser(id = 1), "newpassword") shouldBe stubUser(id = 1, password = "encoded-password")
    }
}
