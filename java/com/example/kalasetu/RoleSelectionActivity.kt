package com.example.kalasetu

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class RoleSelectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_role_selection)


        // 🔁 AUTO REDIRECT IF ALREADY LOGGED IN (USER / ARTIST ONLY)
        val session = SessionManager(this)

        if (session.isLoggedIn()) {

            val role = session.getRole()
            val username = session.getUsername()

            if (role == "USER") {
                val intent = Intent(this, UserDashboardActivity::class.java)
                intent.putExtra("USERNAME", username)
                startActivity(intent)
                finish()
                return
            }

            if (role == "ARTIST") {
                val intent = Intent(this, ArtistDashboardActivity::class.java)
                intent.putExtra("USERNAME", username)
                startActivity(intent)
                finish()
                return
            }
        }


        // 🔘 Role cards
        val cardUser = findViewById<CardView>(R.id.cardUser)
        val cardArtist = findViewById<CardView>(R.id.cardArtist)
        val cardAdmin = findViewById<CardView>(R.id.cardAdmin)

        cardUser.setOnClickListener {
            openLogin("USER")
        }

        cardArtist.setOnClickListener {
            openLogin("ARTIST")
        }

        cardAdmin.setOnClickListener {
            openLogin("ADMIN")
        }
    }

    private fun openLogin(role: String) {
        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtra("ROLE", role)
        startActivity(intent)
    }
}
