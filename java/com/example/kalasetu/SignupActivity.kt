package com.example.kalasetu

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class SignupActivity : AppCompatActivity() {

    private val PHONE_REGEX = Regex("^[0-9]{10}$")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_singup)

        val role = intent.getStringExtra("ROLE") ?: ""

        if (role == "ADMIN") {
            toast("Admin account cannot be created")
            finish()
            return
        }

        val etName = findViewById<EditText>(R.id.etName)
        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPhone = findViewById<EditText>(R.id.etPhone)
        val etAddress = findViewById<EditText>(R.id.etAddress) // ✅ NEW
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etSecretCode = findViewById<EditText>(R.id.etSecretCode)

        val btnSignup = findViewById<Button>(R.id.btnSignup)
        val txtLoginRedirect = findViewById<TextView>(R.id.txtLoginRedirect)

        val db = DBHelper(this)

        // ✅ SAFE: ensure address column exists

        btnSignup.setOnClickListener {

            val name = etName.text.toString().trim()
            val username = etUsername.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val address = etAddress.text.toString().trim() // ✅ NEW
            val password = etPassword.text.toString().trim()
            val secretCode = etSecretCode.text.toString().trim()

            if (name.isEmpty() || username.isEmpty() ||
                phone.isEmpty() || address.isEmpty() ||
                password.isEmpty() || secretCode.isEmpty()
            ) {
                toast("Please fill all fields")
                return@setOnClickListener
            }

            if (!PHONE_REGEX.matches(phone)) {
                toast("Phone number must be 10 digits")
                return@setOnClickListener
            }

            if (db.isUsernameExists(username)) {
                toast("Username already exists")
                return@setOnClickListener
            }

            val success = if (role == "ARTIST") {
                db.insertArtist(name, username, password, phone,address, secretCode)
            } else {
                // ✅ USER signup with address
                val values = ContentValues().apply {
                    put("name", name)
                    put("username", username)
                    put("password", password)
                    put("phone", phone)
                    put("address",address)
                    put("secret_code", secretCode)
                    put("address", address)
                }
                db.writableDatabase.insert("users", null, values) != -1L
            }

            if (success) {
                toast("Signup successful")
                startActivity(
                    Intent(this, RoleSelectionActivity::class.java)
                        .putExtra("ROLE", role)
                )
                finish()
            } else {
                toast("Signup failed, phone number already registered")
            }
        }

        txtLoginRedirect.setOnClickListener {
            startActivity(
                Intent(this, LoginActivity::class.java)
                    .putExtra("ROLE", role)
            )
            finish()
        }
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
