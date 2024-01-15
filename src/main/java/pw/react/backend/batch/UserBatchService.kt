package pw.react.backend.batch

import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import pw.react.backend.dao.UserRepository
import pw.react.backend.models.entity.UserEntity
import pw.react.backend.services.UserMainService

class UserBatchService(
    userRepository: UserRepository,
    passwordEncoder: PasswordEncoder,
    private val batchRepository: BatchRepository<UserEntity>
) : UserMainService(userRepository, passwordEncoder) {

    override fun batchSave(users: Collection<UserEntity>): Collection<UserEntity> {
        log.info("Batch insert.")
        return if (users.isNotEmpty()) {
            val insertedUsers = batchRepository.insertAll(
                users.onEach { it.password = passwordEncoder.encode(it.password) }
            )
            userRepository.findAllByEmailIn(insertedUsers.map(UserEntity::email))
        } else {
            log.warn("User collection is empty or null.")
            emptyList()
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(UserBatchService::class.java)
    }
}
