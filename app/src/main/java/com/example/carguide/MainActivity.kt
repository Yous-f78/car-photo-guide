package com.example.carguide

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.carguide.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Main (and only) Activity.
 *
 * Responsibilities:
 *  1. Request CAMERA permission at runtime.
 *  2. Start CameraX preview + image capture use-cases (rear camera).
 *  3. Let the user pick a [CarType] via chip buttons; updates the overlay.
 *  4. On "Capture", save a full-resolution JPEG to MediaStore (public,
 *     no overlay) with a timestamped filename.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null

    /** Runtime permission launcher for CAMERA. */
    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) startCamera()
            else {
                Toast.makeText(this, R.string.perm_denied, Toast.LENGTH_LONG).show()
                binding.tvStatus.text = getString(R.string.perm_denied)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraExecutor = Executors.newSingleThreadExecutor()

        // --- Car-type selection -------------------------------------------------
        binding.chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            val id = checkedIds.firstOrNull() ?: binding.chipBerline.id
            binding.guideOverlay.carType = when (id) {
                binding.chipCitadine.id -> CarType.CITADINE
                binding.chipSuv.id      -> CarType.SUV
                else                    -> CarType.BERLINE
            }
        }
        // Default selection = Berline
        binding.chipBerline.isChecked = true
        binding.guideOverlay.carType = CarType.BERLINE

        // --- Capture button -----------------------------------------------------
        binding.btnCapture.setOnClickListener { takePhoto() }

        // --- Permission + camera start ------------------------------------------
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    /** Bind CameraX preview + image-capture to the lifecycle. */
    private fun startCamera() {
        val providerFuture = ProcessCameraProvider.getInstance(this)
        providerFuture.addListener({
            val provider = providerFuture.get()

            // 4:3 aspect ratio on preview so the overlay matches what the user sees.
            val preview = Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .build().also {
                    it.setSurfaceProvider(binding.previewView.surfaceProvider)
                }

            // ImageCapture: NO aspect ratio constraint -> uses full sensor resolution.
            // Set JPEG quality to 100 for maximum fidelity.
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .setJpegQuality(100)
                .build()

            try {
                // Unbind any previous use-cases before rebinding.
                provider.unbindAll()
                provider.bindToLifecycle(
                    this,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageCapture
                )
                binding.tvStatus.text = ""
            } catch (e: Exception) {
                Log.e(TAG, "Camera bind failed", e)
                binding.tvStatus.text = "Camera error: ${e.message}"
            }
        }, ContextCompat.getMainExecutor(this))
    }

    /** Capture a photo and save it to MediaStore (no overlay burned in). */
    private fun takePhoto() {
        val capture = imageCapture ?: run {
            Toast.makeText(this, "Camera not ready", Toast.LENGTH_SHORT).show()
            return
        }

        // Build a timestamped filename.
        val name = "car_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis())}.jpg"

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CarPhotoGuide")
            }
        }

        val output = ImageCapture.OutputFileOptions
            .Builder(
                contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            .build()

        binding.btnCapture.isEnabled = false
        capture.takePicture(
            output,
            cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(result: ImageCapture.OutputFileResults) {
                    runOnUiThread {
                        binding.btnCapture.isEnabled = true
                        binding.tvStatus.text = "${getString(R.string.saved_prefix)} $name"
                        Toast.makeText(
                            this@MainActivity,
                            "Saved: $name",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Capture failed", exc)
                    runOnUiThread {
                        binding.btnCapture.isEnabled = true
                        binding.tvStatus.text = "Capture failed: ${exc.message}"
                    }
                }
            }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object { private const val TAG = "CarPhotoGuide" }
}
