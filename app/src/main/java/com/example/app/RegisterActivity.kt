package com.example.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.app.database.DataHelper

class RegisterActivity : AppCompatActivity() {

    private lateinit var dbHelper: DataHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        dbHelper = DataHelper(this)

        val buttonRegister = findViewById<Button>(R.id.buttonRegister)
        val editUserNameRegister = findViewById<EditText>(R.id.editUserNameRegister)
        val editPassRegister = findViewById<EditText>(R.id.editPassRegister)
        val editCheckPassRegister = findViewById<EditText>(R.id.editCheckPassRegister)

        buttonRegister.setOnClickListener {
            val username = editUserNameRegister.text.toString()
            val password = editPassRegister.text.toString()
            val checkPassword = editCheckPassRegister.text.toString()

            if(password == checkPassword){
                if (dbHelper.isUserExists(username)) {
                    Toast.makeText(this, "Tên đăng nhập đã tồn tại", Toast.LENGTH_SHORT).show()
                } else {
                    dbHelper.insertUser(username, password)
                    Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }
            }else{
                Toast.makeText(this, "Mật khẩu không trùng khớp", Toast.LENGTH_SHORT).show()
            }
        }

    }
}