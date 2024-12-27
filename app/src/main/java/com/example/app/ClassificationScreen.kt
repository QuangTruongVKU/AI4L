package com.example.app

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app.adapter.ResultAdapter
import com.example.app.database.CassificationDataHelper
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ClassificationScreen : Activity() {

    private lateinit var classifyModel: Interpreter
    private lateinit var classdbHelper: CassificationDataHelper
    private var diseaseList: List<CassificationDataHelper.Disease> = emptyList()
    private lateinit var imageBitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_classification_screen)

        val btnBack = findViewById<Button>(R.id.imageButton)
        btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val byteArray = intent.getByteArrayExtra("imageBitmap")
        if (byteArray != null) {
            imageBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        } else {
            Toast.makeText(this, "Failed to load image!", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Khởi tạo SQLite Database helper
        classdbHelper = CassificationDataHelper(this)

        // Load YOLO và Classification mô hình TensorFlow Lite
        classifyModel = loadModel("model_float32.tflite")

        // Truy vấn danh sách bệnh từ cơ sở dữ liệu
        diseaseList = classdbHelper.getAllDiseases()


        val predictedIndex = classifyDisease(imageBitmap)
        Log.d("ClassificationScreen", "Predicted Index: $predictedIndex")
        showDiseaseInfo(predictedIndex)

    }

    private fun loadModel(modelFileName: String): Interpreter {
        val modelFile = assets.open(modelFileName).use {
            val modelSize = it.available()
            val modelBuffer = ByteArray(modelSize)
            it.read(modelBuffer)
            ByteBuffer.allocateDirect(modelSize).apply {
                order(ByteOrder.nativeOrder())
                put(modelBuffer)
            }
        }
        return Interpreter(modelFile)
    }


    private fun resizeBitmap(bitmap: Bitmap, targetWidth: Int, targetHeight: Int): Bitmap {
        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
    }


    private fun processImage(bitmap: Bitmap): ByteBuffer {
        val resizedBitmap = resizeBitmap(bitmap, 224, 224)  // Resize to 224x224
        val byteBuffer =
            ByteBuffer.allocateDirect(4 * 224 * 224 * 3)  // 4 bytes per channel * 3 channels (RGB)
        byteBuffer.order(ByteOrder.nativeOrder())

        val pixels = IntArray(224 * 224)
        resizedBitmap.getPixels(pixels, 0, 224, 0, 0, 224, 224)

        for (pixel in pixels) {
            val r = (pixel shr 16 and 0xFF) / 255.0f
            val g = (pixel shr 8 and 0xFF) / 255.0f
            val b = (pixel and 0xFF) / 255.0f

            byteBuffer.putFloat(r)
            byteBuffer.putFloat(g)
            byteBuffer.putFloat(b)
        }

        return byteBuffer
    }


    private fun classifyDisease(bitmap: Bitmap): Int {
        // Resize ảnh về kích thước yêu cầu của mô hình phân loại (224x224)
        val inputBuffer = processImage(bitmap)

        // Chuẩn bị output buffer cho các class scores
        val outputBuffer = Array(1) { FloatArray(diseaseList.size) }

        // Thực hiện dự đoán
        classifyModel.run(inputBuffer, outputBuffer)

        // Tìm index có giá trị cao nhất (ứng với bệnh dự đoán)
        return outputBuffer[0].indices.maxByOrNull { outputBuffer[0][it] } ?: -1
    }


    private fun showDiseaseInfo(predictedIndex: Int) {
        val disease = diseaseList.getOrNull(predictedIndex)
        if (disease != null) {
            val data = listOf(
                "Tên bệnh: " to disease.className,
                "Mô tả: " to disease.description,
                "Tác nhân: " to disease.reason,
                "Triệu chứng: " to disease.symptom,
                "Tác hại: " to disease.damage,
                "Cách phòng ngừa" to disease.prevention,
                "Cách điều trị: " to disease.treatment
            )

            val imageView = findViewById<ImageView>(R.id.imageResult)
            val witdh = 380
            val height = 245

            val image = Bitmap.createScaledBitmap(imageBitmap, witdh, height, true)
            imageView.setImageBitmap(image)


            // Code để hiển thị thông tin bệnh, nếu cần
            val recylerView = findViewById<RecyclerView>(R.id.ResultRecycler)
            recylerView.adapter = ResultAdapter(data as List<Pair<String, String>>)
            recylerView.layoutManager = LinearLayoutManager(this)


        } else {
            // Hiển thị thông tin khi không tìm được bệnh
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        classifyModel.close()
    }
}