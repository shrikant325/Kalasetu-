package com.example.kalasetu

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.github.chrisbanes.photoview.PhotoView

class ArtistOrderDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_artist_order_details)

        // ✅ ONLY ORDER ID IS REQUIRED
        val orderId = intent.getIntExtra("ORDER_ID", -1)
        if (orderId == -1) {
            finish()
            return
        }

        val db = DBHelper(this)

        // USER DETAILS
        val txtUserName = findViewById<TextView>(R.id.txtUserName)
        val txtUserUsername = findViewById<TextView>(R.id.txtUserUsername)
        val txtUserPhone = findViewById<TextView>(R.id.txtUserPhone)
        val txtUserAddress = findViewById<TextView>(R.id.txtUserAddress)

        // PAINTING DETAILS
        val imgPainting = findViewById<PhotoView>(R.id.imgPainting)
        val txtTitle = findViewById<TextView>(R.id.txtTitle)
        val txtDescription = findViewById<TextView>(R.id.txtDescription)
        val txtPrice = findViewById<TextView>(R.id.txtPrice)

        val btnConfirm = findViewById<Button>(R.id.btnConfirm)
        val btnReject = findViewById<Button>(R.id.btnReject)

        // ✅ LOAD FULL ORDER DETAILS USING ORDER ID
        val cursor = db.getOrderDetailsByOrderId(orderId)
        if (cursor.moveToFirst()) {

            // USER
            txtUserName.text = cursor.getString(0)
            txtUserUsername.text = "@${cursor.getString(1)}"
            txtUserPhone.text = cursor.getString(2)
            txtUserAddress.text = cursor.getString(3)

            // PAINTING
            txtTitle.text = cursor.getString(4)
            txtDescription.text = cursor.getString(5)
            txtPrice.text = "₹ ${cursor.getDouble(6)}"

            val imageUri = cursor.getString(7)
            if (!imageUri.isNullOrEmpty()) {
                imgPainting.setImageURI(Uri.parse(imageUri))
            }
        }
        cursor.close()

        // ✅ CONFIRM ORDER
        btnConfirm.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Confirm Order")
                .setMessage("Confirm this order?\nPainting will be delivered in a week.")
                .setPositiveButton("Confirm") { _, _ ->
                    db.updateOrderStatus(orderId, "CONFIRMED")
                    Toast.makeText(
                        this,
                        "Order confirmed. User notified.",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        // ✅ REJECT ORDER
        btnReject.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Reject Order")
                .setMessage("Reject this order?\nPainting Out Of Stock Or delivery Issue .")
                .setPositiveButton("Reject") { _, _ ->
                    db.updateOrderStatus(orderId, "REJECTED")
                    Toast.makeText(
                        this,
                        "Order rejected. User notified.",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
}