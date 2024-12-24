package com.example.app

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.Manifest
import android.annotation.SuppressLint
import android.util.Log
import android.widget.EditText
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.ui.setupWithNavController
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity() {
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_PICK = 2
    private var photoURI: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
//        Search news
        val searchButton = findViewById<ImageButton>(R.id.buttonSearch)
        val textSearch = findViewById<EditText>(R.id.editSearch)



//        ------------------------------------------------
        checkAndRequestPermissions()

        val navController = findNavController(R.id.fragmentContainerView3)
        val buttonNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        buttonNavigationView.setupWithNavController(navController)

        val buttonCamera = findViewById<ImageButton>(R.id.buttonCamera)

        // Mở camera khi nhấn vào nút camera
        buttonCamera.setOnClickListener {
            openCamera()
        }
    }

    // Kiểm tra và yêu cầu quyền camera
    private fun checkAndRequestPermissions() {
        val cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val readExternalStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

        if (cameraPermission != PackageManager.PERMISSION_GRANTED || readExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissionsLauncher.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE))
        }
    }

    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.CAMERA] == true && permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true) {
            Toast.makeText(this, "Permissions granted!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Permissions denied!", Toast.LENGTH_SHORT).show()
        }
    }

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as? Bitmap
            if (imageBitmap != null) {
                handleCapturedImage(imageBitmap)
            } else {
                Toast.makeText(this, "Failed to capture image!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Camera operation cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val selectedImageUri = result.data?.data
            if (selectedImageUri != null) {
                handleSelectedImage(selectedImageUri)
            } else {
                Toast.makeText(this, "Failed to select image!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            takePictureLauncher.launch(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("CameraError", "Failed to launch camera: ${e.message}")
        }
    }

    private fun handleCapturedImage(bitmap: Bitmap) {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()

        val intent = Intent(this, ClassificationScreen::class.java)
        intent.putExtra("imageBitmap", byteArray)
        startActivity(intent)
    }

    private fun handleSelectedImage(uri: Uri) {
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()

        // Giải phóng bộ nhớ Bitmap sau khi chuyển đổi
        bitmap.recycle()

        val intent = Intent(this, ClassificationScreen::class.java)
        intent.putExtra("imageBitmap", byteArray)
        startActivity(intent)
    }


}
