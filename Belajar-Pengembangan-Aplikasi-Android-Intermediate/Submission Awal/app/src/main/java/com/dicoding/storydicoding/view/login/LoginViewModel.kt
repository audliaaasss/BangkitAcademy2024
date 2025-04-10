package com.dicoding.storydicoding.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storydicoding.data.pref.UserModel
import com.dicoding.storydicoding.data.repository.UserRepository
import com.dicoding.storydicoding.data.response.LoginResponse
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> = _loginResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.login(email, password)

                if (response.error == false && response.loginResult?.token != null) {
                    val userModel = UserModel(
                        email = email,
                        token = response.loginResult.token,
                        isLogin = true
                    )
                    repository.saveSession(userModel)
                }
                _loginResult.value = Result.success(response)
            } catch (e: Exception) {
                _loginResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }
}