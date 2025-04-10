package com.dicoding.storydicoding.data.repository

import com.dicoding.storydicoding.data.api.AuthService
import com.dicoding.storydicoding.data.pref.UserModel
import com.dicoding.storydicoding.data.pref.UserPreference
import com.dicoding.storydicoding.data.response.LoginResponse
import com.dicoding.storydicoding.data.response.RegisterResponse
import kotlinx.coroutines.flow.Flow

class UserRepository private constructor(
    private val authService: AuthService,
    private val userPreference: UserPreference
) {
    suspend fun register(name: String, email: String, password: String): RegisterResponse {
        return authService.register(name, email, password)
    }

    suspend fun login(email: String, password: String): LoginResponse {
        return authService.login(email, password)
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            authService: AuthService,
            userPreference: UserPreference
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(authService, userPreference)
            }.also { instance = it }
    }
}