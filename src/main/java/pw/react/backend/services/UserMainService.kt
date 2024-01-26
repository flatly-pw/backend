package pw.react.backend.services

import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import pw.react.backend.dao.UserRepository
import pw.react.backend.exceptions.UserValidationException
import pw.react.backend.models.domain.User
import pw.react.backend.models.domain.toDomain
import pw.react.backend.models.domain.toEntity
import pw.react.backend.models.entity.UserEntity
import kotlin.jvm.optionals.getOrNull

open class UserMainService(
    protected val userRepository: UserRepository,
    protected val passwordEncoder: PasswordEncoder
) : UserService {

    override fun findUserByEmail(email: String): User? {
        return userRepository.findByEmail(email).getOrNull()?.toDomain()
    }

    override fun validateAndSave(user: User): User {
        requireValidUser(user)
        log.info("User is valid")
        val dbUser = userRepository.findByEmail(user.email).getOrNull()?.let(UserEntity::toDomain)
        val userToSave = dbUser?.let {
            log.info("User already exists. Updating it.")
            it.copy(password = passwordEncoder.encode(user.password))
        } ?: user
        return userRepository.save(userToSave.toEntity()).toDomain().also { log.info("User was saved") }
    }

    override fun updatePassword(user: User, password: String): User {
        requireValidUser(user)
        log.debug("Encoding password.")
        val updatedUser = user.copy(password = passwordEncoder.encode(password))
        return userRepository.save(updatedUser.toEntity()).toDomain()
    }

    override fun saveUnique(user: User): User {
        requireValidUser(user)
        log.info("User is valid")
        val dbUser = userRepository.findByEmail(user.email)
        if (dbUser.isPresent) {
            log.error("User already exists")
            throw UserValidationException("User already exists.")
        }
        return batchSave(listOf(user)).toList().first()
    }

    override fun batchSave(users: List<User>): List<User> = if (users.isNotEmpty()) {
        val updatedUsers = users.map { user ->
            requireValidUser(user)
            user.copy(password = passwordEncoder.encode(user.password))
        }
        userRepository.saveAll(updatedUsers.map(User::toEntity)).map(UserEntity::toDomain)
    } else {
        log.warn("User collection is empty or null.")
        emptyList()
    }

    private fun requireValidUser(user: User) {
        when {
            user.email.isEmpty() -> {
                log.error("Empty email.")
                throw UserValidationException("Empty email.")
            }

            !isNonEmptyAndBlank(user.name) -> {
                log.error("Name is empty or blank")
                throw UserValidationException("Name is empty or blank")
            }

            !isNonEmptyAndBlank(user.lastName) -> {
                log.error("Last name is empty or blank")
                throw UserValidationException("Last name is empty or blank")
            }

            !isValidMail(user.email) -> {
                log.error("\'${user.email}\' is not a valid mail")
                throw UserValidationException("\'${user.email}\' is not a valid mail")
            }

            !isValidPassword(user.password) -> {
                log.error("Password length must be between $MIN_PASSWORD_LENGTH and $MAX_PASSWORD_LENGTH")
                throw UserValidationException("Password length must be between $MIN_PASSWORD_LENGTH and $MAX_PASSWORD_LENGTH")
            }
        }
    }

    private fun isNonEmptyAndBlank(value: String) = value.isNotEmpty() && value.isNotBlank()

    private fun isValidMail(mail: String): Boolean {
        val mailRegex = Regex("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$", RegexOption.IGNORE_CASE)
        return mail.matches(mailRegex)
    }

    private fun isValidPassword(password: String) = (MIN_PASSWORD_LENGTH..MAX_PASSWORD_LENGTH).contains(password.length)

    companion object {
        private val log = LoggerFactory.getLogger(UserMainService::class.java)
        private const val MIN_PASSWORD_LENGTH = 8
        private const val MAX_PASSWORD_LENGTH = 32
    }
}
