package com.dicoding.storydicoding.view.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storydicoding.data.response.ErrorResponse
import com.dicoding.storydicoding.databinding.ActivitySignupBinding
import com.dicoding.storydicoding.view.ViewModelFactory
import com.google.gson.Gson
import retrofit2.HttpException
import java.io.IOException

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var viewModel: SignupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(this)
        )[SignupViewModel::class.java]

        setupView()
        setupAction()
        observeViewModel()
        playAnimation()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.register(name, email, password)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.registrationResult.observe(this) { result ->
            result.onSuccess { response ->
                if (response.error == false) {
                    showSuccessDialog(response.message ?: "Registration successful")
                } else {
                    showErrorToast(response.message ?: "Registration failed")
                }
            }.onFailure { exception ->
                handleRegistrationError(exception)
            }
        }
    }

    private fun showSuccessDialog(message: String) {
        AlertDialog.Builder(this).apply {
            setTitle("Success!")
            setMessage(message)
            setPositiveButton("Login") { _, _ ->
                finish()
            }
            create()
            show()
        }
    }

    private fun showErrorToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun handleRegistrationError(exception: Throwable) {
        val errorMessage = when (exception) {
            is HttpException -> {
                try {
                    val jsonInString = exception.response()?.errorBody()?.string()
                    val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                    errorBody?.message ?: "Registration failed due to server error"
                } catch (e: Exception) {
                    "Error parsing server response"
                }
            }
            is IOException -> "Network error. Please check your connection"
            else -> exception.message ?: "An unexpected error occurred"
        }
        showErrorToast(errorMessage)
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val tvName = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(100)
        val edName = ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val tvEmail = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val edEmail = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val tvPassword = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val edPassword = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(title, tvName, edName, tvEmail, edEmail, tvPassword, edPassword)
            start()
        }
    }
}