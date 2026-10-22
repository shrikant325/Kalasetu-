package com.example.kalasetu

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AdminApproveRejectPaintingActivity : AppCompatActivity() {

    private lateinit var db: DBHelper
    private var paintingId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_approve_reject_painting)

        paintingId = intent.getIntExtra("PAINTING_ID", -1)
        if (paintingId == -1) {
            Toast.makeText(this, "Invalid painting", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        db = DBHelper(this)

        val imgPainting = findViewById<ImageView>(R.id.imgPainting)
        val txtTitle = findViewById<TextView>(R.id.txtTitle)
        val txtDescription = findViewById<TextView>(R.id.txtDescription)
        val txtPrice = findViewById<TextView>(R.id.txtPrice)
        val btnApprove = findViewById<Button>(R.id.btnApprove)
        val btnReject = findViewById<Button>(R.id.btnReject)

        // 🔥 LOAD PAINTING DETAILS
        val cursor = db.getPaintingById(paintingId)
        if (cursor.moveToFirst()) {
            txtTitle.text = cursor.getString(0)
            txtDescription.text = cursor.getString(1)
            txtPrice.text = "₹ ${cursor.getDouble(2)}"

            val imageUri = cursor.getString(3)
            imgPainting.setImageURI(Uri.parse(imageUri))
        }
        cursor.close()

        btnApprove.setOnClickListener {
            db.updatePaintingStatus(paintingId, "APPROVED")
            Toast.makeText(this, "Painting Approved", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnReject.setOnClickListener {
            db.updatePaintingStatus(paintingId, "REJECTED")
            Toast.makeText(this, "Painting Rejected", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
