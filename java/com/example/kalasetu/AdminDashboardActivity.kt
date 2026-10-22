package com.example.kalasetu

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class AdminDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        val txtAdminName = findViewById<TextView>(R.id.txtAdminName)
        val txtAdminUsername = findViewById<TextView>(R.id.txtAdminUsername)

        val btnLogout = findViewById<Button>(R.id.btnLogout)
        val btnViewUsers = findViewById<Button>(R.id.btnViewUsers)
        val btnViewArtists = findViewById<Button>(R.id.btnViewArtists)

        // ✅ THIS IS THE ONLY NEW LINE (Approve Painting Button)
        val btnApprovePainting = findViewById<Button>(R.id.btnApprovePainting)

        txtAdminName.text = "Admin"
        txtAdminUsername.text = "@admin"

        // View Users
        btnViewUsers.setOnClickListener {
            startActivity(Intent(this, AdminViewUsersActivity::class.java))
        }

        // View Artists
        btnViewArtists.setOnClickListener {
            startActivity(Intent(this, AdminViewArtistsActivity::class.java))
        }

        // ✅ APPROVE PAINTING (STEP 1 – ONLY NAVIGATION)
        btnApprovePainting.setOnClickListener {
            android.util.Log.d("ADMIN_FLOW", "Approve Painting clicked")
            startActivity(Intent(this, AdminPendingArtistsActivity::class.java))
        }


        // Logout
        btnLogout.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Do you really want to logout?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                SessionManager(this).logout()

                val intent = Intent(this, RoleSelectionActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
