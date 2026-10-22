package com.example.kalasetu

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AdminViewArtistsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var db: DBHelper
    private val artistList = mutableListOf<ArtistModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_view_artists)

        recyclerView = findViewById(R.id.recyclerArtists)
        recyclerView.layoutManager = LinearLayoutManager(this)

        db = DBHelper(this)
        loadArtists()
    }

    private fun loadArtists() {
        artistList.clear()

        val cursor: Cursor = db.getArtists()

        if (cursor.moveToFirst()) {
            do {
                val name = cursor.getString(0)
                val username = cursor.getString(1)
                val phone = cursor.getString(2)

                artistList.add(ArtistModel(name, username, phone))
            } while (cursor.moveToNext())
        }

        cursor.close()
        recyclerView.adapter = ArtistAdapter(artistList, db)
    }

    // ---------- MODEL ----------
    data class ArtistModel(
        val name: String,
        val username: String,
        val phone: String
    )

    // ---------- ADAPTER ----------
    class ArtistAdapter(
        private val list: MutableList<ArtistModel>,
        private val db: DBHelper
    ) : RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder>() {

        class ArtistViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val name: TextView = view.findViewById(R.id.txtArtistName)
            val username: TextView = view.findViewById(R.id.txtArtistUsername)
            val phone: TextView = view.findViewById(R.id.txtArtistPhone)
            val deleteBtn: Button = view.findViewById(R.id.btnDeleteArtist)
            val clickLayout: View = view.findViewById(R.id.layoutArtistClick)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_artist, parent, false)
            return ArtistViewHolder(view)
        }

        override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
            val artist = list[position]

            holder.name.text = artist.name
            holder.username.text = "@${artist.username}"
            holder.phone.text = artist.phone

            // Open artist paintings
            holder.clickLayout.setOnClickListener {
                val context = holder.itemView.context
                val intent = Intent(context, AdminArtistPaintingsActivity::class.java)
                intent.putExtra("ARTIST_USERNAME", artist.username)
                context.startActivity(intent)
            }

            // Delete artist
            holder.deleteBtn.setOnClickListener {
                AlertDialog.Builder(holder.itemView.context)
                    .setTitle("Delete Artist")
                    .setMessage("Do you want to delete this artist and all paintings?")
                    .setPositiveButton("Yes") { _, _ ->
                        db.writableDatabase.delete(
                            "paintings",
                            "artist_username=?",
                            arrayOf(artist.username)
                        )
                        db.deleteArtist(artist.username)
                        list.removeAt(position)
                        notifyItemRemoved(position)
                    }
                    .setNegativeButton("No", null)
                    .show()
            }
        }

        override fun getItemCount(): Int = list.size
    }
}
