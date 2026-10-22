package com.example.kalasetu

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class UserBrowsePaintingsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var db: DBHelper
    private val list = mutableListOf<PaintingModel>()
    private lateinit var loggedInUsername: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_browse_paintings)
        loggedInUsername = intent.getStringExtra("USERNAME") ?: ""
        recyclerView = findViewById(R.id.recyclerBrowsePaintings)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        db = DBHelper(this)
        loadPaintings()
    }

    private fun loadPaintings() {
        list.clear()

        val cursor: Cursor = db.getApprovedPaintings()
        if (cursor.moveToFirst()) {
            do {
                list.add(
                    PaintingModel(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow("painting_id")),
                        title = cursor.getString(cursor.getColumnIndexOrThrow("title")),
                        artist = cursor.getString(cursor.getColumnIndexOrThrow("artist_username")),
                        price = cursor.getDouble(cursor.getColumnIndexOrThrow("price")),
                        imageUri = cursor.getString(cursor.getColumnIndexOrThrow("image_uri")) ?: ""
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()

        recyclerView.adapter = PaintingAdapter(list)
    }

    data class PaintingModel(
        val id: Int,
        val title: String,
        val artist: String,
        val price: Double,
        val imageUri: String
    )

    inner class PaintingAdapter(private val data: List<PaintingModel>) :
        RecyclerView.Adapter<PaintingAdapter.VH>() {

        inner class VH(v: View) : RecyclerView.ViewHolder(v) {
            val img: ImageView = v.findViewById(R.id.imgPainting)
            val title: TextView = v.findViewById(R.id.txtTitle)
            val artist: TextView = v.findViewById(R.id.txtArtist)
            val price: TextView = v.findViewById(R.id.txtPrice)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_user_painting, parent, false)
            return VH(v)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val p = data[position]

            holder.title.text = p.title
            holder.artist.text = "@${p.artist}"
            holder.price.text = "₹ ${p.price}"

            // SAFE IMAGE LOAD
            try {
                if (p.imageUri.isNotBlank()) {
                    holder.img.setImageURI(Uri.parse(p.imageUri))
                } else {
                    holder.img.setImageResource(android.R.color.darker_gray)
                }
            } catch (e: Exception) {
                holder.img.setImageResource(android.R.color.darker_gray)
            }

            holder.itemView.setOnClickListener {
                val intent = Intent(
                    this@UserBrowsePaintingsActivity,
                    UserPaintingDetailsActivity::class.java
                )
                intent.putExtra("PAINTING_ID", p.id)
                intent.putExtra("USERNAME", loggedInUsername) // ✅ THIS WAS MISSING
                startActivity(intent)
            }
        }

        override fun getItemCount(): Int = data.size
    }
}