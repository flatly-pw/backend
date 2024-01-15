package pw.react.backend.services

import pw.react.backend.models.entity.UserEntity

interface UserService {
    fun validateAndSave(user: UserEntity): UserEntity
    fun updatePassword(user: UserEntity, password: String): UserEntity
    fun saveUnique(user: UserEntity): UserEntity
    fun batchSave(users: Collection<UserEntity>): Collection<UserEntity>
}
