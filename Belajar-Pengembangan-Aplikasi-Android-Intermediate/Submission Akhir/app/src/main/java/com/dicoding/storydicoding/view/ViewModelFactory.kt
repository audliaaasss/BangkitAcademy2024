package com.dicoding.storydicoding.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storydicoding.data.repository.StoryRepository
import com.dicoding.storydicoding.data.repository.UserRepository
import com.dicoding.storydicoding.di.Injection
import com.dicoding.storydicoding.view.add.AddStoryViewModel
import com.dicoding.storydicoding.view.detail.DetailViewModel
import com.dicoding.storydicoding.view.login.LoginViewModel
import com.dicoding.storydicoding.view.main.MainViewModel
import com.dicoding.storydicoding.view.maps.MapsViewModel
import com.dicoding.storydicoding.view.signup.SignupViewModel

class ViewModelFactory(
    private val userRepository: UserRepository,
    private val storyRepository: StoryRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(userRepository, storyRepository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(SignupViewModel::class.java) -> {
                SignupViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                DetailViewModel(storyRepository) as T
            }
            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
                AddStoryViewModel(storyRepository) as T
            }
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(storyRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null
        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    val userRepository = Injection.provideUserRepository(context)
                    val storyRepository = Injection.provideStoryRepository(context)
                    INSTANCE = ViewModelFactory(userRepository, storyRepository)
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }
}