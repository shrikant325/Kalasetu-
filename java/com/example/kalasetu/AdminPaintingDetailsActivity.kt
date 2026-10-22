package com.example.kalasetu

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class AdminPaintingDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_painting_details)

        val paintingId = intent.getIntExtra("PAINTING_ID", -1)
        if (paintingId == -1) {
            finish()
            return
        }

        val db = DBHelper(this)
        val cursor = db.readableDatabase.rawQuery(
            "SELECT title, description, price, image_uri FROM paintings WHERE painting_id=?",
            arrayOf(paintingId.toString())
        )

        if (cursor.moveToFirst()) {
            val title = cursor.getString(0)
            val desc = cursor.getString(1)
            val price = cursor.getDouble(2)
            val imagePath = cursor.getString(3)

            findViewById<TextView>(R.id.txtTitle).text = title
            findViewById<TextView>(R.id.txtDescription).text = desc
            findViewById<TextView>(R.id.txtPrice).text = "₹ $price"

            val img = findViewById<ImageView>(R.id.imgPainting)
            val file = File(imagePath)

            if (file.exists()) {
                img.setImageBitmap(
                    BitmapFactory.decodeFile(file.absolutePath)
                )
            } else {
                img.setImageResource(android.R.drawable.ic_menu_report_image)
            }
        }

        cursor.close()
    }
}
