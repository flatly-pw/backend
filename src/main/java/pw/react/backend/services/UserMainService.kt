package pw.react.backend.services

import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import pw.react.backend.dao.UserRepository
import pw.react.backend.exceptions.UserValidationException
import pw.react.backend.models.entity.UserEntity

open class UserMainService(
    protected val userRepository: UserRepository,
    protected val passwordEncoder: PasswordEncoder
) : UserService {

    override fun validateAndSave(user: UserEntity): UserEntity {
        var user = user
        if (isValidUser(user)) {
            log.info("User is valid")
            val dbUser = userRepository.findByEmail(user.username)
            if (dbUser.isPresent) {
                log.info("User already exists. Updating it.")
                user.id = dbUser.get().id
                user.password = passwordEncoder.encode(user.password)
            }
            user = userRepository.save(user)
            log.info("User was saved.")
        }
        return user
    }

    override fun updatePassword(user: UserEntity, password: String): UserEntity {
        var user = user
        if (isValidUser(user)) {
            log.debug("Encoding password.")
            user.password = passwordEncoder.encode(password)
            user = userRepository.save(user)
        }
        return user
    }

    override fun saveUnique(user: UserEntity): UserEntity {
        val dbUser = userRepository.findByEmail(user.email)
        if (dbUser.isPresent) {
            log.error("User already exists")
            throw UserValidationException("User already exists.")
        }
        return batchSave(listOf(user)).toList().first()
    }

    override fun batchSave(users: Collection<UserEntity>): Collection<UserEntity> {
        return if (users.isNotEmpty()) {
            for (user in users) {
                isValidUser(user)
                user.password = passwordEncoder!!.encode(user.password)
            }
            userRepository.saveAll(users)
        } else {
            log.warn("User collection is empty or null.")
            emptyList()
        }
    }

    private fun isValidUser(user: UserEntity): Boolean {
        if (isValid(user.username)) {
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
        return true

    }

    private fun isValid(value: String) = value.isBlank()

    companion object {
        private val log = LoggerFactory.getLogger(UserMainService::class.java)
    }
}
