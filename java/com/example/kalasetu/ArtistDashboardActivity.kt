package com.example.kalasetu

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class ArtistDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_artist_dashboard)

        val username = intent.getStringExtra("USERNAME") ?: ""

        if (username.isEmpty()) {
            Toast.makeText(this, "Session error. Login again.", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, RoleSelectionActivity::class.java))
            finish()
            return
        }

        val txtName = findViewById<TextView>(R.id.txtName)
        val txtUsername = findViewById<TextView>(R.id.txtUsername)
        val txtPhone = findViewById<TextView>(R.id.txtPhone)
        val txtRole = findViewById<TextView>(R.id.txtRole)
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        val btnDelete = findViewById<Button>(R.id.btnDelete)
        val btnUpload = findViewById<Button>(R.id.btnUploadPainting)
        val btnMyPaintings = findViewById<Button>(R.id.btnMyPaintings)
        val btnViewOrders = findViewById<Button>(R.id.btnViewOrders)

        val dbHelper = DBHelper(this)

        dbHelper.getArtistDetails(username)?.let {
            txtName.text = it.first
            txtUsername.text = "@${it.second}"
            txtPhone.text = "Phone: ${it.third}"
            txtRole.text = "Role: ARTIST"
        }

        btnLogout.setOnClickListener {
            showLogoutDialog()
        }

        btnDelete.setOnClickListener {
            showDeleteDialog(username)
        }

        btnUpload.setOnClickListener {
            val intent = Intent(this, UploadPaintingActivity::class.java)
            intent.putExtra("ARTIST_USERNAME", username)
            startActivity(intent)
        }

        // ✅ FIXED HERE (THIS WAS THE BUG)
        btnMyPaintings.setOnClickListener {
            val intent = Intent(this, ArtistMyPaintingsActivity::class.java)
            intent.putExtra("ARTIST_USERNAME", username)
            startActivity(intent)
        }
        btnViewOrders.setOnClickListener {
            val i = Intent(this, ArtistOrdersActivity::class.java)
            i.putExtra("ARTIST_USERNAME", username)
            startActivity(i)
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
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun showDeleteDialog(username: String) {
        AlertDialog.Builder(this)
            .setTitle("Delete Account")
            .setMessage("This action is permanent.\nDo you want to continue?")
            .setCancelable(false)
            .setPositiveButton("Delete") { _, _ ->
                DBHelper(this).deleteArtist(username)
                SessionManager(this).logout()
                val intent = Intent(this, RoleSelectionActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
