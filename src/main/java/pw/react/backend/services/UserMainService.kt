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
        if (isValid(user.email)) {
            log.error("Empty mail.")
            throw UserValidationException("Empty mail.")
        }
        if (isValid(user.password)) {
            log.error("Empty user password.")
            throw UserValidationException("Empty user password.")
        }
        if (isValid(user.email)) {
            log.error("UEmpty email.")
            throw UserValidationException("Empty email.")
        }
    }

    private fun isValid(value: String) = value.isBlank()

    companion object {
        private val log = LoggerFactory.getLogger(UserMainService::class.java)
    }
}
