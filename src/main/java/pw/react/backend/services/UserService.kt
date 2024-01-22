package pw.react.backend.services

import pw.react.backend.models.domain.User


interface UserService {
    fun findUserByEmail(email: String): User?
    fun validateAndSave(user: User): User
    fun updatePassword(user: User, password: String): User
    fun saveUnique(user: User): User
    fun batchSave(users: List<User>): List<User>
}
