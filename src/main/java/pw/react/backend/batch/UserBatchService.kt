package pw.react.backend.batch

import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import pw.react.backend.dao.UserRepository
import pw.react.backend.models.domain.toDomain
import pw.react.backend.models.domain.toEntity
import pw.react.backend.models.entity.UserEntity
import pw.react.backend.services.UserMainService

class UserBatchService(
    userRepository: UserRepository,
    passwordEncoder: PasswordEncoder,
    private val batchRepository: BatchRepository<UserEntity>
) : UserMainService(userRepository, passwordEncoder) {

    override fun batchSave(users: Collection<UserEntity>): Collection<UserEntity> {
        val users = users.map { it.toDomain() }
        log.info("Batch insert.")
        return if (users.isNotEmpty()) {
            val insertedUsers = batchRepository.insertAll(
                users.map { it.copy(password = passwordEncoder.encode(it.password)).toEntity() }
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
