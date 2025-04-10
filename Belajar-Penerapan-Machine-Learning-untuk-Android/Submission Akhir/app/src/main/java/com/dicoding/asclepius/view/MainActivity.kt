package com.dicoding.asclepius.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import org.tensorflow.lite.task.vision.classifier.Classifications

class MainActivity : AppCompatActivity(), ImageClassifierHelper.ClassifierListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var imageClassifierHelper: ImageClassifierHelper

    private var currentImageUri: Uri? = null
    private var classificationResult: String? = null

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                showToast("Permission request granted")
            } else {
                showToast("Permission request denied")
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageClassifierHelper = ImageClassifierHelper(
            context = this,
            classifierListener = this
        )

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.analyzeButton.setOnClickListener {
            currentImageUri?.let {
                analyzeImage(it)
            } ?: run {
                showToast(getString(R.string.empty_image_warning))
                moveToResult()
            }
        }
    }

    private fun startGallery() {
        // TODO: Mendapatkan gambar dari Gallery.
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

    private fun showImage() {
        // TODO: Menampilkan gambar sesuai Gallery yang dipilih.
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun analyzeImage(uri: Uri) {
        // TODO: Menganalisa gambar yang berhasil ditampilkan.
        imageClassifierHelper.classifyStaticImage(this, uri)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Simpan currentImageUri sebagai string
        outState.putString("currentImageUri", currentImageUri?.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Pulihkan currentImageUri dari bundle jika tersedia
        currentImageUri = savedInstanceState.getString("currentImageUri")?.let { Uri.parse(it) }
        // Tampilkan kembali gambar jika currentImageUri tidak null
        currentImageUri?.let {
            showImage()
        }
    }

    override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
        val resultText = StringBuilder()
        var isCancerDetected = false
        var detectedLabel = ""
        var confidenceScore = 0.0

        results?.firstOrNull()?.categories?.forEach { category ->
            val percentageScore = String.format("%.2f", category.score * 100)
            resultText.append("Label: ${category.label}, Confidence: $percentageScore%\n")

            // Tentukan apakah gambar menunjukkan tanda-tanda kanker
            if (category.label.equals("Cancer", ignoreCase = true)) {
                isCancerDetected = true
                detectedLabel = category.label
                confidenceScore = category.score.toDouble()
            }
        }

        classificationResult = if (isCancerDetected && confidenceScore >= 0.5) {
            "Cancer Detected\nLabel: $detectedLabel\nConfidence: ${String.format("%.2f", confidenceScore * 100)}%"
        } else {
            "No Cancer Detected\n${resultText}"
        }

        Log.d("Classification Result", "Result: $classificationResult")

        // Pindah ke ResultActivity dengan hasil klasifikasi
        moveToResult()
    }

    override fun onError(error: String) {
        showToast(error)
    }

    private fun moveToResult() {
        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra(ResultActivity.EXTRA_IMAGE_URI, currentImageUri.toString())
            putExtra(ResultActivity.EXTRA_RESULT, classificationResult)
        }
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}