package com.example.kalasetu

import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AdminArtistPaintingsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var db: DBHelper
    private val paintingList = mutableListOf<PaintingModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_artist_paintings)

        val artistUsername = intent.getStringExtra("ARTIST_USERNAME") ?: return

        recyclerView = findViewById(R.id.recyclerPaintings)
        recyclerView.layoutManager = LinearLayoutManager(this)

        db = DBHelper(this)

        loadPaintings(artistUsername)
    }

    private fun loadPaintings(username: String) {
        paintingList.clear()

        val cursor: Cursor = db.getPaintingsByArtist(username)

        if (cursor.moveToFirst()) {
            do {
                paintingList.add(
                    PaintingModel(
                        id = cursor.getInt(0),
                        title = cursor.getString(1),
                        description = cursor.getString(2),
                        price = cursor.getDouble(3),
                        imagePath = cursor.getString(4)
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()

        recyclerView.adapter = PaintingAdapter(paintingList)
    }

    // -------- MODEL --------
    data class PaintingModel(
        val id: Int,
        val title: String,
        val description: String,
        val price: Double,
        val imagePath: String
    )

    // -------- ADAPTER --------
    class PaintingAdapter(
        private val list: List<PaintingModel>
    ) : RecyclerView.Adapter<PaintingAdapter.PaintingViewHolder>() {

        class PaintingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val txtTitle: TextView = view.findViewById(R.id.txtPaintingTitle)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaintingViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_painting, parent, false)
            return PaintingViewHolder(view)
        }

        override fun onBindViewHolder(holder: PaintingViewHolder, position: Int) {
            val painting = list[position]
            holder.txtTitle.text = painting.title

            holder.itemView.setOnClickListener {
                val intent = android.content.Intent(
                    holder.itemView.context,
                    AdminPaintingDetailsActivity::class.java
                )
                intent.putExtra("PAINTING_ID", painting.id)
                holder.itemView.context.startActivity(intent)
            }
        }

        override fun getItemCount(): Int = list.size
    }
}
