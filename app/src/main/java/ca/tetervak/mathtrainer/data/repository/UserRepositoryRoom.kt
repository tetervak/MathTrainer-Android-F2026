package ca.tetervak.mathtrainer.data.repository

import ca.tetervak.mathtrainer.data.database.dao.UserDao
import ca.tetervak.mathtrainer.data.database.entity.UserEntity


class UserRepositoryRoom(
    private val userDao: UserDao
) {

    suspend fun getCurrentUser(): UserEntity {
        return userDao.getFirstUser()
            ?: throw IllegalStateException("No user found. Call ensureDefaultUser() first.")
    }

    suspend fun ensureDefaultUser(): UserEntity {
        val existing = userDao.getFirstUser()
        if (existing != null) return existing

        val defaultUser = UserEntity.demoUser
        userDao.insert(defaultUser)
        return defaultUser
    }
}