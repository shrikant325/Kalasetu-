package com.example.kalasetu

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class UserDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_dashboard)

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
        val btnBrowsePaintings = findViewById<Button>(R.id.btnBrowsePaintings)

        // ✅ FIX (THIS WAS MISSING)
        val btnMyOrders = findViewById<Button>(R.id.btnMyOrders)

        val dbHelper = DBHelper(this)

        // Cursor-based reading
        val cursor = dbHelper.getUserDetails(username)
        if (cursor.moveToFirst()) {
            txtName.text = cursor.getString(0)
            txtUsername.text = "@${cursor.getString(1)}"
            txtPhone.text = "Phone: ${cursor.getString(2)}"
            txtRole.text = "Role: USER"
        }
        cursor.close()

        btnBrowsePaintings.setOnClickListener {
            startActivity(
                Intent(this, UserBrowsePaintingsActivity::class.java)
                    .putExtra("USERNAME", username)
            )
        }

        // ✅ NOW WORKING
        btnMyOrders.setOnClickListener {
            val intent = Intent(this, UserMyOrdersActivity::class.java)
            intent.putExtra("USERNAME", username)
            startActivity(intent)
        }

        btnLogout.setOnClickListener {
            showLogoutDialog()
        }

        btnDelete.setOnClickListener {
            showDeleteDialog(username)
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
                DBHelper(this).deleteUser(username)
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