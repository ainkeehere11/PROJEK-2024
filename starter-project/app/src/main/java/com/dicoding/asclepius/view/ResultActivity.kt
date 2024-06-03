package com.dicoding.asclepius.view

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.dicoding.asclepius.riwayat.HistoryDatabase
import com.dicoding.asclepius.riwayat.HistoryPrediction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.io.File
import java.io.FileOutputStream

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    companion object {
        const val IMAGE_URI = "img_uri"
        const val TAG = "imagePicker"
        const val RESULT_TXT = "result_text"
        const val UPDATE_HISTORY = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // TODO: Menampilkan hasil gambar, prediksi, dan confidence score.
        val imageUriString = intent.getStringExtra(IMAGE_URI)
        if (imageUriString != null) {
            val imageUri = Uri.parse(imageUriString)
            displayImage(imageUri)

            val imageClassifierHelper = ImageClassifierHelper(
                contextValue = this,
                classifierListenerValue = object : ImageClassifierHelper.ClassifierListener {
                    override fun onError(errorMessage: String) {
                        Log.d(TAG, "Error: $errorMessage")
                    }

                    override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                        results?.let { showResults(it) }
                    }
                }
            )
            imageClassifierHelper.classifyStaticImage(imageUri)
        } else {
            Log.e(TAG, "No image URI provided")
            finish()
        }

        binding.save.setOnClickListener {
            val imageUriString = intent.getStringExtra(IMAGE_URI)
            val result = binding.resultText.text.toString()

            if (imageUriString != null) {
                val imageUri = Uri.parse(imageUriString)
                showToast("Data saved")
                savePredictionToDatabase(imageUri, result)
            } else {
                showToast("No image URI provided")
                finish()
            }
        }
    }

    private fun savePredictionToDatabase(imageUri: Uri, result: String) {
        if (result.isNotEmpty()) {
            val fileName = "cropped_image_${System.currentTimeMillis()}.jpg"
            val destinationUri = Uri.fromFile(File(cacheDir, fileName))
            contentResolver.openInputStream(imageUri)?.use { input ->
                FileOutputStream(File(cacheDir, fileName)).use { output ->
                    input.copyTo(output)
                }
            }
            val prediction = HistoryPrediction(imagePath = destinationUri.toString(), result = result)
            GlobalScope.launch(Dispatchers.IO) {
                val database = HistoryDatabase.getDatabase(applicationContext)
                try {
                    database.predictionHistoryDao().insertPrediction(prediction)
                    Log.d(TAG, "Prediction saved successfully: $prediction")
                    val predictions = database.predictionHistoryDao().getAllPredictions()
                    Log.d(TAG, "All predictions after save: $predictions")
                    moveToHistory(destinationUri, result)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to save prediction: $prediction", e)
                }
            }
        } else {
            Log.e(TAG, "Result is empty, cannot save prediction to database.")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun displayImage(uri: Uri) {
        Log.d(TAG, "Displaying image: $uri")
        binding.resultImage.setImageURI(uri)
    }

    private fun showResults(results: List<Classifications>) {
        val topResult = results.firstOrNull()
        topResult?.let { result ->
            val label = result.categories.firstOrNull()?.label ?: ""
            val score = result.categories.firstOrNull()?.score ?: 0f
            binding.resultText.text = "$label ${score.formatToString()}"
        }
    }

    private fun Float.formatToString(): String {
        return String.format("%.2f%%", this * 100)
    }

    private fun moveToHistory(imageUri: Uri, result: String) {
        val intent = Intent(this, ActivityHistory::class.java)
        intent.putExtra(RESULT_TXT, result)
        intent.putExtra(IMAGE_URI, imageUri.toString())
        setResult(RESULT_OK, intent)
        startActivity(intent)
        finish()
    }

}