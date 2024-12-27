package com.example.app.Fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.app.ClassificationScreen
import com.example.app.R
import com.example.app.databinding.FragmentCameraBinding
import java.io.ByteArrayOutputStream

class CameraFragment : Fragment() {
    private lateinit var binding: FragmentCameraBinding
    private var capturedImageBitmap: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkAndRequestPermissions()

        binding.buttonCamera.setOnClickListener {
            openCamera()
        }

        binding.btnGallery.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
        }

        binding.buttonAccept.setOnClickListener {
            capturedImageBitmap?.let {
                navigateToClassification(it)
            } ?: Toast.makeText(requireContext(), "No image selected or captured!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkAndRequestPermissions() {
        val cameraPermission = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.CAMERA
        )
        val readExternalStoragePermission = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE
        )

        if (cameraPermission != PackageManager.PERMISSION_GRANTED ||
            readExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissionsLauncher.launch(
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
            )
        }
    }

    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.CAMERA] == true &&
            permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true) {
            Toast.makeText(requireContext(), "Permissions granted!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Permissions denied!", Toast.LENGTH_SHORT).show()
        }
    }

    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as? Bitmap
            if (imageBitmap != null) {
                displayCapturedImage(imageBitmap)
            } else {
                Toast.makeText(requireContext(), "Failed to capture image!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Camera operation cancelled", Toast.LENGTH_SHORT).show()
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

    private fun displayCapturedImage(bitmap: Bitmap) {
        capturedImageBitmap = bitmap
        binding.imageView3.setImageBitmap(bitmap)
    }

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val selectedImageUri = result.data?.data
            if (selectedImageUri != null) {
                handleSelectedImage(selectedImageUri)
            } else {
                Toast.makeText(requireContext(), "Failed to select image!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleSelectedImage(uri: Uri) {
        val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
        capturedImageBitmap = bitmap
        binding.imageView3.setImageBitmap(bitmap)
    }

    private fun navigateToClassification(bitmap: Bitmap) {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()

        val intent = Intent(requireContext(), ClassificationScreen::class.java)
        intent.putExtra("imageBitmap", byteArray)
        startActivity(intent)
    }
}
