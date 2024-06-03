package com.dicoding.asclepius.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.yalantis.ucrop.UCrop
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var currentImageUri: Uri? = null
    private var croppedImageUri: Uri? = null
    private lateinit var navigationView: BottomNavigationView

    companion object {
        const val TAG = "ImagePick"
        private const val REQUEST_RESULT = 2000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navigationView = findViewById(R.id.menuBar)
        navigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.main -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.history -> {
                    startActivity(Intent(this, ActivityHistory::class.java))
                    true
                }
                else -> false
            }
        }

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.analyzeButton.setOnClickListener {
            currentImageUri?.let {
                analyzeImage()
                moveToResult()
            } ?: run {
                showToast(getString(R.string.image_classifier_failed))
            }
        }
    }

    private val startGalleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                currentImageUri = uri
                showImage()
                startUCrop(uri)
            } ?: showToast("Failed to get image URI")
        }
    }

    private fun startGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        startGalleryLauncher.launch(chooser)
    }


    private fun startUCrop(sourceUri: Uri) {
        val fileName = "cropped_image_${System.currentTimeMillis()}.jpg"
        val destinationUri = Uri.fromFile(File(cacheDir, fileName))
        UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(1000, 1000)
            .start(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            val resultUri = UCrop.getOutput(data!!)
            resultUri?.let {
                showCroppedImage(resultUri)
            } ?: showToast("Failed to crop image")
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            showToast("Crop error: ${cropError?.message}")
        }
    }

    private fun showImage() {
        currentImageUri?.let { uri ->
            Log.d(TAG, "Displaying image: $uri")
            binding.previewImageView.setImageURI(uri)
        } ?: Log.d(TAG, "No image to display")
    }

    private inline fun moveToResultOrShowToast(action: (Intent) -> Unit) {
        val intent = Intent(this, ResultActivity::class.java)
        croppedImageUri?.let { uri ->
            intent.putExtra(ResultActivity.IMAGE_URI, uri.toString())
            action(intent)
        } ?: showToast(getString(R.string.image_classifier_failed))
    }

    private fun analyzeImage() {
        moveToResultOrShowToast { intent ->
            startActivityForResult(intent, REQUEST_RESULT)
        }
    }

    private fun moveToResult() {
        moveToResultOrShowToast { intent ->
            Log.d(TAG, "Moving to ResultActivity")
            startActivity(intent)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    private fun showCroppedImage(uri: Uri) {
        binding.previewImageView.setImageURI(uri)
        croppedImageUri = uri
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

}