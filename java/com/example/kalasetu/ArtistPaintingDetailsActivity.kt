package com.example.kalasetu

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ArtistPaintingDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_artist_painting_details)

        val paintingId = intent.getIntExtra("PAINTING_ID", -1)
        if (paintingId == -1) {
            finish()
            return
        }

        val db = DBHelper(this)

        val imgPainting = findViewById<ImageView>(R.id.imgPainting)
        val txtTitle = findViewById<TextView>(R.id.txtTitle)
        val txtDescription = findViewById<TextView>(R.id.txtDescription)
        val txtPrice = findViewById<TextView>(R.id.txtPrice)
        val txtStatus = findViewById<TextView>(R.id.txtStatus)

        val cursor = db.readableDatabase.rawQuery(
            """
            SELECT title, description, price, image_uri, status
            FROM paintings
            WHERE painting_id = ?
            """,
            arrayOf(paintingId.toString())
        )

        if (cursor.moveToFirst()) {

            txtTitle.text = cursor.getString(0)
            txtDescription.text = cursor.getString(1)
            txtPrice.text = "₹ ${cursor.getDouble(2)}"

            val imageUri = cursor.getString(3)
            val statusValue = cursor.getString(4)

            txtStatus.text = "Status: $statusValue"

            // ✅ THIS IS THE CRITICAL FIX
            if (!imageUri.isNullOrEmpty()) {
                imgPainting.setImageURI(Uri.parse(imageUri))
            }
        }

        cursor.close()
    }
}
