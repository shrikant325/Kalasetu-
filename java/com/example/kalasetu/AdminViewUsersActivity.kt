package com.example.kalasetu

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AdminViewUsersActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_view_users)

        val recyclerView = findViewById<RecyclerView>(R.id.rvUsers)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val db = DBHelper(this)
        val cursor = db.readableDatabase.rawQuery(
            "SELECT name, username, phone FROM users",
            null
        )

        recyclerView.adapter = AdminUserAdapter(this, cursor)
    }
}
