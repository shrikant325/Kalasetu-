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

class AdminPendingPaintingsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var db: DBHelper
    private val paintingList = mutableListOf<PaintingModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_pending_paintings)

        val artistUsername = intent.getStringExtra("ARTIST_USERNAME")
        if (artistUsername.isNullOrEmpty()) {
            finish()
            return
        }

        recyclerView = findViewById(R.id.recyclerPendingPaintings)
        recyclerView.layoutManager = LinearLayoutManager(this)

        db = DBHelper(this)
        loadPendingPaintings(artistUsername)
    }

    private fun loadPendingPaintings(username: String) {
        paintingList.clear()

        val cursor: Cursor = db.readableDatabase.rawQuery(
            """
            SELECT painting_id, title
            FROM paintings
            WHERE artist_username = ?
            AND status = 'PENDING'
            ORDER BY created_at DESC
            """,
            arrayOf(username)
        )

        if (cursor.moveToFirst()) {
            do {
                paintingList.add(
                    PaintingModel(
                        id = cursor.getInt(0),
                        title = cursor.getString(1)
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        recyclerView.adapter = PendingPaintingAdapter(paintingList)
    }

    // -------- MODEL --------
    data class PaintingModel(
        val id: Int,
        val title: String
    )

    // -------- ADAPTER --------
    inner class PendingPaintingAdapter(
        private val list: List<PaintingModel>
    ) : RecyclerView.Adapter<PendingPaintingAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val txtTitle: TextView = view.findViewById(R.id.txtPaintingTitle)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_pending_painting, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val painting = list[position]
            holder.txtTitle.text = painting.title

            holder.itemView.setOnClickListener {
                val intent = Intent(
                    this@AdminPendingPaintingsActivity,
                    AdminApproveRejectPaintingActivity::class.java
                )
                intent.putExtra("PAINTING_ID", painting.id)
                startActivity(intent)
            }
        }

        override fun getItemCount(): Int = list.size
    }
}
