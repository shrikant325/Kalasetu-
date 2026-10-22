package com.example.kalasetu

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ArtistMyPaintingsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var db: DBHelper
    private val paintingList = mutableListOf<PaintingModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_artist_my_paintings)

        val artistUsername = intent.getStringExtra("ARTIST_USERNAME")
        if (artistUsername.isNullOrEmpty()) {
            finish()
            return
        }

        recyclerView = findViewById(R.id.recyclerMyPaintings)
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
                        status = cursor.getString(5)
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        recyclerView.adapter = MyPaintingsAdapter(paintingList)
    }

    data class PaintingModel(
        val id: Int,
        val title: String,
        val status: String
    )

    inner class MyPaintingsAdapter(
        private val list: List<PaintingModel>
    ) : RecyclerView.Adapter<MyPaintingsAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val title: TextView = view.findViewById(R.id.txtPaintingTitle)
            val status: TextView = view.findViewById(R.id.txtPaintingStatus)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_artist_my_painting, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val painting = list[position]

            holder.title.text = painting.title
            holder.status.text = painting.status

            holder.itemView.setOnClickListener {
                val intent = Intent(
                    this@ArtistMyPaintingsActivity,
                    ArtistPaintingDetailsActivity::class.java
                )
                intent.putExtra("PAINTING_ID", painting.id)
                startActivity(intent)
            }
        }

        override fun getItemCount(): Int = list.size
    }
}
