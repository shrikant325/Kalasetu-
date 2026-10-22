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

class AdminPendingArtistsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var db: DBHelper
    private val artistList = mutableListOf<ArtistModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_pending_artists)

        recyclerView = findViewById(R.id.recyclerPendingArtists)
        recyclerView.layoutManager = LinearLayoutManager(this)

        db = DBHelper(this)
        loadArtists()
    }

    private fun loadArtists() {
        artistList.clear()

        val cursor: Cursor = db.getArtistsWithPendingPaintings()

        if (cursor.moveToFirst()) {
            do {
                val name =
                    cursor.getString(cursor.getColumnIndexOrThrow("name"))
                val username =
                    cursor.getString(cursor.getColumnIndexOrThrow("username"))
                val phone =
                    cursor.getString(cursor.getColumnIndexOrThrow("phone"))

                artistList.add(
                    ArtistModel(
                        name = name,
                        username = username,
                        phone = phone
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        recyclerView.adapter = PendingArtistAdapter(artistList)
    }

    // ---------- MODEL ----------
    data class ArtistModel(
        val name: String,
        val username: String,
        val phone: String
    )

    // ---------- ADAPTER ----------
    class PendingArtistAdapter(
        private val list: List<ArtistModel>
    ) : RecyclerView.Adapter<PendingArtistAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val name: TextView = view.findViewById(R.id.txtArtistName)
            val username: TextView = view.findViewById(R.id.txtArtistUsername)
            val phone: TextView = view.findViewById(R.id.txtArtistPhone)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_pending_artist, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val artist = list[position]

            holder.name.text = artist.name
            holder.username.text = "@${artist.username}"
            holder.phone.text = artist.phone

            holder.itemView.setOnClickListener {
                val intent = Intent(
                    holder.itemView.context,
                    AdminPendingPaintingsActivity::class.java
                )
                intent.putExtra("ARTIST_USERNAME", artist.username)
                holder.itemView.context.startActivity(intent)
            }
        }

        override fun getItemCount(): Int = list.size
    }
}
