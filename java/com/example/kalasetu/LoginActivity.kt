package com.example.kalasetu

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private var userRole: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val session = SessionManager(this)
        val dbHelper = DBHelper(this)

        // ======================
        // ROLE FROM INTENT
        // ======================
        userRole = intent.getStringExtra("ROLE")?.uppercase() ?: ""

        val txtLoginTitle = findViewById<TextView>(R.id.txtLoginTitle)
        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val txtSignup = findViewById<TextView>(R.id.txtSignup)
        val txtForgot = findViewById<TextView>(R.id.txtForgot)

        txtLoginTitle.text = when (userRole) {
            "USER" -> "User Login"
            "ARTIST" -> "Artist Login"
            "ADMIN" -> "Admin Login"
            else -> "Login"
        }

        if (userRole == "ADMIN") {
            txtForgot.visibility = View.GONE
        }

        // ======================
        // LOGIN BUTTON
        // ======================
        btnLogin.setOnClickListener {

            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                toast("Enter username and password")
                return@setOnClickListener
            }

            when (userRole) {

                // ---------- ADMIN ----------
                "ADMIN" -> {
                    if (username == "admin" && password == "admin123") {

                        session.saveLogin("ADMIN", "admin")

                        startActivity(
                            Intent(this, AdminDashboardActivity::class.java)
                                .apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK
                                }
                        )
                    } else {
                        toast("Invalid admin credentials")
                    }
                }

                // ---------- USER ----------
                "USER" -> {
                    if (dbHelper.checkUserLogin(username, password)) {

                        session.saveLogin("USER", username)

                        startActivity(
                            Intent(this, UserDashboardActivity::class.java)
                                .putExtra("USERNAME", username)
                                .apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK
                                }
                        )
                    } else {
                        toast("Invalid username or password")
                    }
                }

                // ---------- ARTIST ----------
                "ARTIST" -> {
                    if (dbHelper.checkArtistLogin(username, password)) {

                        session.saveLogin("ARTIST", username)

                        startActivity(
                            Intent(this, ArtistDashboardActivity::class.java)
                                .putExtra("USERNAME", username)
                                .apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK
                                }
                        )
                    } else {
                        toast("Invalid artist credentials")
                    }
                }

                else -> toast("Role error. Restart app.")
            }
        }

        // ======================
        // SIGNUP
        // ======================
        txtSignup.setOnClickListener {
            if (userRole == "ADMIN") {
                toast("Admin account cannot be created")
            } else {
                startActivity(
                    Intent(this, SignupActivity::class.java)
                        .putExtra("ROLE", userRole)
                )
            }
        }

        // ======================
        // FORGOT PASSWORD
        // ======================
        txtForgot.setOnClickListener {
            startActivity(
                Intent(this, ForgotPasswordActivity::class.java)
                    .putExtra("ROLE", userRole)
            )
        }
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
