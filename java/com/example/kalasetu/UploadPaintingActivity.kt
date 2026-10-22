package com.example.kalasetu

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream

class UploadPaintingActivity : AppCompatActivity() {

    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_painting)

        val artistUsername = intent.getStringExtra("ARTIST_USERNAME")

        if (artistUsername.isNullOrEmpty()) {
            Toast.makeText(this, "Session error. Login again.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val etTitle = findViewById<EditText>(R.id.etPaintingTitle)
        val etDescription = findViewById<EditText>(R.id.etDescription)
        val etPrice = findViewById<EditText>(R.id.etPrice)
        val btnUpload = findViewById<Button>(R.id.btnUpload)

        val btnSelectImage = findViewById<Button>(R.id.btnSelectImage)
        val imgPreview = findViewById<ImageView>(R.id.imgPreview)

        val dbHelper = DBHelper(this)

        // =========================
        // IMAGE PICKER
        // =========================
        val imagePicker =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    selectedImageUri = result.data?.data
                    imgPreview.setImageURI(selectedImageUri)
                }
            }

        btnSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            imagePicker.launch(intent)
        }

        // =========================
        // UPLOAD BUTTON
        // =========================
        btnUpload.setOnClickListener {

            val title = etTitle.text.toString().trim()
            val description = etDescription.text.toString().trim()
            val priceText = etPrice.text.toString().trim()

            if (title.isEmpty() || priceText.isEmpty()) {
                Toast.makeText(this, "Title and price are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedImageUri == null) {
                Toast.makeText(this, "Please select painting image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val price = priceText.toDoubleOrNull()
            if (price == null || price <= 0) {
                Toast.makeText(this, "Enter valid price", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ✅ IMPORTANT FIX: copy image to internal storage
            val internalImagePath =
                saveImageToInternalStorage(this, selectedImageUri!!)

            val success = dbHelper.uploadPainting(
                artistUsername = artistUsername,
                title = title,
                description = description,
                price = price,
                imageUri = internalImagePath   // ✅ STORE FILE PATH
            )

            if (success) {
                Toast.makeText(
                    this,
                    "Painting uploaded successfully",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            } else {
                Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // =========================
    // SAVE IMAGE INTERNALLY
    // =========================
    private fun saveImageToInternalStorage(context: Context, uri: Uri): String {

        val fileName = "painting_${System.currentTimeMillis()}.jpg"
        val file = File(context.filesDir, fileName)

        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalStateException("Unable to open input stream")

        val outputStream = FileOutputStream(file)

        val buffer = ByteArray(1024)
        var length: Int

        while (true) {
            length = inputStream.read(buffer)
            if (length <= 0) break
            outputStream.write(buffer, 0, length)
        }

        outputStream.flush()
        inputStream.close()
        outputStream.close()

        // 🔎 DEBUG CHECK (IMPORTANT)
        if (!file.exists() || file.length() == 0L) {
            throw IllegalStateException("Image copy failed")
        }

        return file.absolutePath
    }

}
