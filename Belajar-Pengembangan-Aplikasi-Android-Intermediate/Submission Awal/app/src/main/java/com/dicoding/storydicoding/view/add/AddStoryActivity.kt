package com.dicoding.storydicoding.view.add

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.dicoding.storydicoding.R
import com.dicoding.storydicoding.data.api.ApiConfig
import com.dicoding.storydicoding.data.pref.UserPreference
import com.dicoding.storydicoding.data.pref.dataStore
import com.dicoding.storydicoding.data.repository.StoryRepository
import com.dicoding.storydicoding.data.response.AddStoryResponse
import com.dicoding.storydicoding.data.utils.getImageUri
import com.dicoding.storydicoding.data.utils.reduceFileImage
import com.dicoding.storydicoding.data.utils.uriToFile
import com.dicoding.storydicoding.databinding.ActivityAddStoryBinding
import com.dicoding.storydicoding.view.ViewModelFactory
import com.dicoding.storydicoding.view.main.MainViewModel
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private var currentImageUri: Uri? = null
    private lateinit var token: String

    private val viewModel by viewModels<AddStoryViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = UserPreference.getInstance(applicationContext.dataStore)
        runBlocking {
            token = pref.getSession().first().token
        }

        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnCamera.setOnClickListener { startCamera() }
        binding.btnAdd.setOnClickListener { uploadImage() }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri!!)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        } else {
            currentImageUri = null
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.ivAddPhoto.setImageURI(it)
        }
    }

    private fun uploadImage() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            val userDescription = binding.edAddDescription.text.toString().trim()
            val description = userDescription.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )

            showLoading(true)

            viewModel.uploadStory(
                file = multipartBody,
                description = description,
                onSuccess = { response ->
                    response.message?.let { showToast(it) }
                    showLoading(false)
                    finish()
                },
                onError = { error ->
                    if (error is HttpException && error.code() == 401) {
                        showToast(message = String())
                    } else {
                        showToast(error.localizedMessage ?: getString(R.string.upload_failed))
                    }
                    showLoading(false)
                }
            )
        } ?: showToast(getString(R.string.empty_image))
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}