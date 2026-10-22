package com.example.kalasetu

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.github.chrisbanes.photoview.PhotoView

class UserPaintingDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_painting_details)

        val paintingId = intent.getIntExtra("PAINTING_ID", -1)
        val userUsername = intent.getStringExtra("USERNAME") ?: ""

        if (paintingId == -1 || userUsername.isEmpty()) {
            finish()
            return
        }

        val db = DBHelper(this)

        val img = findViewById<PhotoView>(R.id.imgPainting)
        val txtArtist = findViewById<TextView>(R.id.txtArtist)
        val txtTitle = findViewById<TextView>(R.id.txtTitle)
        val txtDesc = findViewById<TextView>(R.id.txtDescription)
        val txtPrice = findViewById<TextView>(R.id.txtPrice)
        val btnBuy = findViewById<Button>(R.id.btnBuy)

        var artistUsername = ""

        val c = db.getPaintingById(paintingId)
        if (c.moveToFirst()) {

            txtTitle.text = c.getString(0)
            txtDesc.text = c.getString(1)
            txtPrice.text = "₹ ${c.getDouble(2)}"

            val imageUri = c.getString(3)
            if (!imageUri.isNullOrEmpty()) {
                img.setImageURI(Uri.parse(imageUri))
            }

            // fetch artist username
            val artistCursor = db.readableDatabase.rawQuery(
                "SELECT artist_username FROM paintings WHERE painting_id=?",
                arrayOf(paintingId.toString())
            )

            if (artistCursor.moveToFirst()) {
                artistUsername = artistCursor.getString(0)

                db.getArtistDetails(artistUsername)?.let {
                    txtArtist.text =
                        "Artist: ${it.first}\n@${it.second}  |  ${it.third}"
                }
            }
            artistCursor.close()
        }
        c.close()

        // ✅ FIXED BUY BUTTON
        btnBuy.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Confirm Purchase")
                .setMessage("Do you want to buy this painting?")
                .setPositiveButton("Yes") { _, _ ->

                    val success = db.placeOrder(
                        paintingId = paintingId,
                        userUsername = userUsername,
                        artistUsername = artistUsername
                    )

                    if (success) {
                        Toast.makeText(
                            this,
                            "Order placed. Waiting for artist confirmation.",
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            "Failed to place order. Try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .setNegativeButton("No", null)
                .show()
        }
    }
}