package com.example.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.app.database.DataHelper
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class LoginActivity : AppCompatActivity() {
    private lateinit var dbHelper: DataHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        dbHelper = DataHelper(this)

        val buttonNextRegister = findViewById<Button>(R.id.buttonNextRegister)
        val buttonLogin = findViewById<Button>(R.id.buttonLogin)
        val editUserNameLogin = findViewById<EditText>(R.id.editUserNameLogin)
        val editPasswordLogin = findViewById<EditText>(R.id.editPasswordLogin)

        // Chuyển đến màn hình đăng ký
        buttonNextRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Xử lý đăng nhập
        buttonLogin.setOnClickListener {

            val username = editUserNameLogin.text.toString()
            val password = editPasswordLogin.text.toString()
            if (dbHelper.checkUser(username, password)) {
                val id = dbHelper.getUserId(username)
                val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putInt("id", id as Int) // Thay 123 bằng ID thực tế của người dùng
                editor.apply() // Hoặc editor.commit() nếu cần đồng bộ ngay

                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                // Điều hướng tới màn hình khác nếu cần
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
