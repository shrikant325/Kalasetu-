package com.example.kalasetu

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var db: DBHelper
    private var role = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        db = DBHelper(this)
        role = intent.getStringExtra("ROLE") ?: ""

        val etPhone = findViewById<EditText>(R.id.etPhone)
        val etSecretCode = findViewById<EditText>(R.id.etSecretCode)
        val etNewPassword = findViewById<EditText>(R.id.etNewPassword)
        val txtRecoveredUsername = findViewById<TextView>(R.id.txtRecoveredUsername)
        val btnRecover = findViewById<Button>(R.id.btnRecover)

        btnRecover.setOnClickListener {

            val phone = etPhone.text.toString().trim()
            val secretCode = etSecretCode.text.toString().trim()
            val newPassword = etNewPassword.text.toString().trim()

            if (phone.length != 10 || secretCode.isEmpty() || newPassword.length < 6) {
                toast("Invalid input")
                return@setOnClickListener
            }

            val username = when (role) {
                "USER" -> db.recoverUser(phone, secretCode)
                "ARTIST" -> db.recoverArtist(phone, secretCode)
                else -> null
            }

            if (username == null) {
                toast("Wrong phone number or secret code")
                return@setOnClickListener
            }

            val success = when (role) {
                "USER" -> db.updateUserPassword(username, newPassword)
                "ARTIST" -> db.updateArtistPassword(username, newPassword)
                else -> false
            }

            if (success) {
                txtRecoveredUsername.text = "Your username: $username"
                toast("Password updated successfully")
            } else {
                toast("Password update failed")
            }
        }
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
