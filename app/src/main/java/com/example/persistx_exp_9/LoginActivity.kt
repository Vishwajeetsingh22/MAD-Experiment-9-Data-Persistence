package com.example.persistx_exp_9

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.edit

class LoginActivity : Activity() {

    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var loginButton: Button
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var prefs: android.content.SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefs = getSharedPreferences("PersistXPrefs", MODE_PRIVATE)

        // Check if already logged in
        val isLoggedIn = prefs.getBoolean("is_logged_in", false)
        if (isLoggedIn) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_login)

        username = findViewById(R.id.etUsername)
        password = findViewById(R.id.etPassword)
        loginButton = findViewById(R.id.btnLogin)
        dbHelper = DatabaseHelper(this)

        // Pre-fill last saved username from SharedPreferences
        val savedUsername = prefs.getString("last_username", "") ?: ""
        username.setText(savedUsername)

        loginButton.setOnClickListener {
            val userStr = username.text.toString().trim()
            val passStr = password.text.toString().trim()

            if (userStr.isEmpty() || passStr.isEmpty()) {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Save username in SharedPreferences so it's persisted on the login page
            prefs.edit {
                putString("last_username", userStr)
                putBoolean("is_logged_in", true)
            }

            // Save user credentials in SQLite database
            val success = dbHelper.insertUser(userStr, passStr)

            if (success) {
                Toast.makeText(this, "Login successful! User saved in SQLite & SharedPreferences", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Error saving login data", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
