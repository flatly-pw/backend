package pw.react.backend.services

import pw.react.backend.models.domain.User


interface UserService {
    fun findById(id: Long): User?
    fun findUserByEmail(email: String): User?
    fun validateAndSave(user: User): User
    fun updateName(user: User, name: String): User
    fun updateLastName(user: User, lastName: String): User
    fun updatePassword(user: User, password: String): User
    fun saveUnique(user: User): User
    fun batchSave(users: List<User>): List<User>
}
