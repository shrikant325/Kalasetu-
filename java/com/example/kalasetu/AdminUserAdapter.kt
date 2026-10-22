package com.example.kalasetu

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class AdminUserAdapter(
    private val context: Context,
    private val cursor: Cursor
) : RecyclerView.Adapter<AdminUserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_admin_user, parent, false)
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int = cursor.count

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        cursor.moveToPosition(position)

        val name = cursor.getString(0)
        val username = cursor.getString(1)
        val phone = cursor.getString(2)

        holder.txtName.text = name
        holder.txtUsername.text = "@$username"
        holder.txtPhone.text = "Phone: $phone"

        holder.btnDelete.setOnClickListener {

            androidx.appcompat.app.AlertDialog.Builder(context)
                .setTitle("Delete User")
                .setMessage(
                    "Do you really want to delete this user?\n\n" +
                            "This action is permanent and cannot be undone."
                )
                .setCancelable(false)
                .setPositiveButton("Yes") { _, _ ->

                    val deleted = DBHelper(context).deleteUser(username)

                    if (deleted) {
                        Toast.makeText(context, "User deleted successfully", Toast.LENGTH_SHORT).show()
                        cursor.requery()
                        notifyDataSetChanged()
                    } else {
                        Toast.makeText(context, "Delete failed", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

    }

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtName: TextView = view.findViewById(R.id.txtName)
        val txtUsername: TextView = view.findViewById(R.id.txtUsername)
        val txtPhone: TextView = view.findViewById(R.id.txtPhone)
        val btnDelete: Button = view.findViewById(R.id.btnDeleteUser)
    }
}
