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

class ArtistOrdersActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var db: DBHelper
    private val orderList = mutableListOf<OrderModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_artist_orders)

        val artistUsername = intent.getStringExtra("ARTIST_USERNAME") ?: return

        recyclerView = findViewById(R.id.recyclerOrders)
        recyclerView.layoutManager = LinearLayoutManager(this)

        db = DBHelper(this)
        loadOrders(artistUsername)
    }

    private fun loadOrders(artistUsername: String) {
        orderList.clear()

        val cursor: Cursor = db.getOrdersForArtistDetailed(artistUsername)
        if (cursor.moveToFirst()) {
            do {
                orderList.add(
                    OrderModel(
                        orderId = cursor.getInt(0),
                        paintingTitle = cursor.getString(1),
                        userName = cursor.getString(2),
                        username = cursor.getString(3),
                        phone = cursor.getString(4),
                        address = cursor.getString(5),
                        status = cursor.getString(6)
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()

        recyclerView.adapter = OrderAdapter(orderList)
    }

    // ---------------- MODEL ----------------
    data class OrderModel(
        val orderId: Int,
        val paintingTitle: String,
        val userName: String,
        val username: String,
        val phone: String,
        val address: String,
        val status: String
    )

    // ---------------- ADAPTER ----------------
    inner class OrderAdapter(private val list: List<OrderModel>) :
        RecyclerView.Adapter<OrderAdapter.VH>() {

        inner class VH(v: View) : RecyclerView.ViewHolder(v) {
            val title: TextView = v.findViewById(R.id.txtPaintingTitle)
            val name: TextView = v.findViewById(R.id.txtUserName)
            val username: TextView = v.findViewById(R.id.txtUsername)
            val phone: TextView = v.findViewById(R.id.txtPhone)
            val address: TextView = v.findViewById(R.id.txtAddress) // ✅ FIXED ID
            val status: TextView = v.findViewById(R.id.txtStatus)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_artist_order, parent, false)
            return VH(v)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val o = list[position]

            holder.title.text = o.paintingTitle
            holder.name.text = o.userName
            holder.username.text = "@${o.username}"
            holder.phone.text = "Phone: ${o.phone}"
            holder.address.text = "Address: ${o.address}" // ✅ FIXED
            holder.status.text = "Status: ${o.status}"

            holder.itemView.setOnClickListener {
                val i = Intent(
                    this@ArtistOrdersActivity,
                    ArtistOrderDetailsActivity::class.java
                )
                i.putExtra("ORDER_ID", o.orderId)
                i.putExtra("USER_USERNAME", o.username)
                startActivity(i) // ✅ REQUIRED
            }
        }

        override fun getItemCount() = list.size
    }
}