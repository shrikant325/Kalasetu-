package com.example.kalasetu

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

class UserMyOrdersActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var db: DBHelper
    private val list = mutableListOf<OrderModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_my_orders)

        val username = intent.getStringExtra("USERNAME") ?: ""
        if (username.isEmpty()) {
            finish()
            return
        }

        recyclerView = findViewById(R.id.recyclerMyOrders)
        recyclerView.layoutManager = LinearLayoutManager(this)

        db = DBHelper(this)
        loadOrders(username)
    }

    private fun loadOrders(username: String) {
        list.clear()

        val cursor: Cursor = db.getUserOrders(username)
        if (cursor.moveToFirst()) {
            do {
                list.add(
                    OrderModel(
                        imageUri = cursor.getString(0),
                        paintingTitle = cursor.getString(1),
                        artistName = cursor.getString(2),
                        artistUsername = cursor.getString(3),
                        artistPhone = cursor.getString(4),
                        price = cursor.getDouble(5),
                        status = cursor.getString(6)
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()

        recyclerView.adapter = OrderAdapter(list)
    }

    // ---------------- MODEL ----------------
    data class OrderModel(
        val imageUri: String,
        val paintingTitle: String,
        val artistName: String,
        val artistUsername: String,
        val artistPhone: String,
        val price: Double,
        val status: String
    )

    // ---------------- ADAPTER ----------------
    inner class OrderAdapter(private val data: List<OrderModel>) :
        RecyclerView.Adapter<OrderAdapter.VH>() {

        inner class VH(v: View) : RecyclerView.ViewHolder(v) {
            val img: ImageView = v.findViewById(R.id.imgPainting)
            val title: TextView = v.findViewById(R.id.txtTitle)
            val artistName: TextView = v.findViewById(R.id.txtArtistName)
            val artistUsername: TextView = v.findViewById(R.id.txtArtistUsername)
            val phone: TextView = v.findViewById(R.id.txtPhone)
            val status: TextView = v.findViewById(R.id.txtStatus)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_user_order, parent, false)
            return VH(v)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val o = data[position]

            // IMAGE
            if (o.imageUri.isNotBlank()) {
                holder.img.setImageURI(Uri.parse(o.imageUri))
            } else {
                holder.img.setImageResource(R.drawable.img_box_bg)
            }

            holder.title.text = o.paintingTitle
            holder.artistName.text = o.artistName
            holder.artistUsername.text = "@${o.artistUsername}"
            holder.phone.text = "📞 ${o.artistPhone}"

            when (o.status.uppercase()) {
                "PENDING" -> {
                    holder.status.text =
                        "Pending – Waiting for artist confirmation"
                    holder.status.setTextColor(
                        holder.itemView.context.getColor(R.color.status_pending)
                    )
                }
                "CONFIRMED" -> {
                    holder.status.text =
                        "Confirmed – Order will be delivered in a week"
                    holder.status.setTextColor(
                        holder.itemView.context.getColor(R.color.status_confirmed)
                    )
                }
                "REJECTED" -> {
                    holder.status.text =
                        "Rejected – Painting Out Of Stock Or Delivery Issue"
                    holder.status.setTextColor(
                        holder.itemView.context.getColor(R.color.status_rejected)
                    )
                }
            }
        }

        override fun getItemCount() = data.size
    }
}