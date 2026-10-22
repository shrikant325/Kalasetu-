package com.example.kalasetu

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

        companion object {
        private const val DATABASE_NAME = "KalaSetu.db"

        // 🔒 IMPORTANT: KEEP THIS FIXED
        private const val DATABASE_VERSION = 7
    }

    // =========================
    // DATABASE CREATION
    // =========================
    override fun onCreate(db: SQLiteDatabase) {

        db.execSQL("""
            CREATE TABLE users (
                user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT,
                username TEXT UNIQUE,
                password TEXT,
                phone TEXT UNIQUE,
                address TEXT,
                secret_code TEXT
            )
        """)

        // ✅ ONLY CHANGE IS HERE → address TEXT added
        db.execSQL("""
            CREATE TABLE artists (
                artist_id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT,
                username TEXT UNIQUE,
                password TEXT,
                phone TEXT UNIQUE,
                address TEXT,
                secret_code TEXT
            )
        """)

        db.execSQL("""
            CREATE TABLE paintings (
                painting_id INTEGER PRIMARY KEY AUTOINCREMENT,
                artist_username TEXT,
                title TEXT,
                description TEXT,
                price REAL,
                image_uri TEXT,
                status TEXT DEFAULT 'PENDING',
                in_stock INTEGER DEFAULT 1,
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP
            )
        """)
        db.execSQL("""
    CREATE TABLE orders (
        order_id INTEGER PRIMARY KEY AUTOINCREMENT,
        painting_id INTEGER,
        user_username TEXT,
        artist_username TEXT,
        status TEXT DEFAULT 'PENDING',
        created_at DATETIME DEFAULT CURRENT_TIMESTAMP
    )
""")
    }

    // =========================
    // DATABASE UPGRADE (SAFE)
    // =========================
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // ❌ DO NOT DROP TABLES
        // ❌ DO NOT RESET DATABASE
    }

    // =========================
    // DATABASE DOWNGRADE (CRASH FIX)
    // =========================
    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // DO NOTHING
    }

    // =========================
    // LOGIN
    // =========================
    fun checkUserLogin(username: String, password: String): Boolean =
        readableDatabase.rawQuery(
            "SELECT 1 FROM users WHERE username=? AND password=?",
            arrayOf(username, password)
        ).use { it.moveToFirst() }

    fun checkArtistLogin(username: String, password: String): Boolean =
        readableDatabase.rawQuery(
            "SELECT 1 FROM artists WHERE username=? AND password=?",
            arrayOf(username, password)
        ).use { it.moveToFirst() }

    // =========================
    // PROFILE
    // =========================
    fun getUserDetails(username: String): Cursor =
        readableDatabase.rawQuery(
            "SELECT name, username, phone, address FROM users WHERE username=?",
            arrayOf(username)
        )

    fun getArtistDetails(username: String): Triple<String, String, String>? =
        readableDatabase.rawQuery(
            "SELECT name, username, phone FROM artists WHERE username=?",
            arrayOf(username)
        ).use {
            if (it.moveToFirst())
                Triple(it.getString(0), it.getString(1), it.getString(2))
            else null
        }

    // =========================
    // DELETE
    // =========================
    fun deleteUser(username: String): Boolean =
        writableDatabase.delete("users", "username=?", arrayOf(username)) > 0

    fun deleteArtist(username: String): Boolean =
        writableDatabase.delete("artists", "username=?", arrayOf(username)) > 0

    // =========================
    // RECOVERY
    // =========================
    fun recoverUser(phone: String, secret: String): String? =
        readableDatabase.rawQuery(
            "SELECT username FROM users WHERE phone=? AND secret_code=?",
            arrayOf(phone, secret)
        ).use { if (it.moveToFirst()) it.getString(0) else null }

    fun recoverArtist(phone: String, secret: String): String? =
        readableDatabase.rawQuery(
            "SELECT username FROM artists WHERE phone=? AND secret_code=?",
            arrayOf(phone, secret)
        ).use { if (it.moveToFirst()) it.getString(0) else null }

    fun updateUserPassword(username: String, pass: String): Boolean =
        writableDatabase.update(
            "users",
            ContentValues().apply { put("password", pass) },
            "username=?",
            arrayOf(username)
        ) > 0

    fun updateArtistPassword(username: String, pass: String): Boolean =
        writableDatabase.update(
            "artists",
            ContentValues().apply { put("password", pass) },
            "username=?",
            arrayOf(username)
        ) > 0

    // =========================
    // PAINTINGS
    // =========================
    fun uploadPainting(
        artistUsername: String,
        title: String,
        description: String,
        price: Double,
        imageUri: String
    ): Boolean =
        writableDatabase.insert(
            "paintings",
            null,
            ContentValues().apply {
                put("artist_username", artistUsername)
                put("title", title)
                put("description", description)
                put("price", price)
                put("image_uri", imageUri)
                put("status", "PENDING")
                put("in_stock", 1)
            }
        ) != -1L

    fun getPendingPaintings(): Cursor =
        readableDatabase.rawQuery(
            "SELECT * FROM paintings WHERE status='PENDING'",
            null
        )

    fun getApprovedPaintings(): Cursor =
        readableDatabase.rawQuery(
            "SELECT * FROM paintings WHERE status='APPROVED' AND in_stock=1",
            null
        )

    fun getArtistPaintings(username: String): Cursor =
        readableDatabase.rawQuery(
            "SELECT * FROM paintings WHERE artist_username=?",
            arrayOf(username)
        )

    fun updatePaintingStatus(id: Int, status: String): Boolean =
        writableDatabase.update(
            "paintings",
            ContentValues().apply { put("status", status) },
            "painting_id=?",
            arrayOf(id.toString())
        ) > 0

    // =========================
    // USERNAME CHECK
    // =========================
    fun isUsernameExists(username: String): Boolean {
        val cursor = readableDatabase.rawQuery(
            """
            SELECT username FROM users WHERE username=?
            UNION
            SELECT username FROM artists WHERE username=?
            """,
            arrayOf(username, username)
        )
        val exists = cursor.moveToFirst()
        cursor.close()
        return exists
    }

    // =========================
    // SIGNUP
    // =========================
    fun insertUser(
        name: String,
        username: String,
        password: String,
        phone: String,
        address: String,
        secretCode: String
    ): Boolean =
        writableDatabase.insert(
            "users",
            null,
            ContentValues().apply {
                put("name", name)
                put("username", username)
                put("password", password)
                put("phone", phone)
                put("address", address)
                put("secret_code", secretCode)
            }
        ) != -1L

    fun insertArtist(
        name: String,
        username: String,
        password: String,
        phone: String,
        address: String,
        secretCode: String
    ): Boolean =
        writableDatabase.insert(
            "artists",
            null,
            ContentValues().apply {
                put("name", name)
                put("username", username)
                put("password", password)
                put("phone", phone)
                put("address",address)
                put("secret_code", secretCode)
            }
        ) != -1L

    // =========================
    // ADMIN
    // =========================
    fun getAllUsers(): Cursor =
        readableDatabase.rawQuery(
            "SELECT name, username, phone, address FROM users ORDER BY name ASC",
            null
        )

    fun getAllArtists(): Cursor =
        readableDatabase.rawQuery(
            "SELECT artist_id AS _id, name, username, phone FROM artists",
            null
        )

    fun getArtistById(artistId: Long): Cursor =
        readableDatabase.rawQuery(
            "SELECT artist_id AS _id, name, username, phone FROM artists WHERE artist_id=?",
            arrayOf(artistId.toString())
        )

    fun getPaintingsByArtist(username: String): Cursor =
        readableDatabase.rawQuery(
            """
            SELECT painting_id, title, description, price, image_uri, status, created_at
            FROM paintings
            WHERE artist_username=?
            ORDER BY created_at DESC
            """,
            arrayOf(username)
        )

    fun getArtists(): Cursor =
        readableDatabase.rawQuery(
            "SELECT name, username, phone FROM artists ORDER BY name",
            null
        )

    fun getArtistsWithPendingPaintings(): Cursor =
        readableDatabase.rawQuery(
            """
            SELECT DISTINCT 
                a.name,
                a.username,
                a.phone
            FROM artists a
            INNER JOIN paintings p
                ON a.username = p.artist_username
            WHERE p.status = 'PENDING'
            ORDER BY a.name ASC
            """,
            null
        )

    fun getPaintingById(paintingId: Int): Cursor =
        readableDatabase.rawQuery(
            """
        SELECT title, description, price, image_uri, artist_username
        FROM paintings
        WHERE painting_id = ?
        """,
            arrayOf(paintingId.toString())
        )
    // =========================
// ORDERS
// =========================
    fun placeOrder(
        paintingId: Int,
        userUsername: String,
        artistUsername: String
    ): Boolean =
        writableDatabase.insert(
            "orders",
            null,
            ContentValues().apply {
                put("painting_id", paintingId)
                put("user_username", userUsername)
                put("artist_username", artistUsername)
                put("status", "PENDING")
            }
        ) != -1L

    fun getOrdersForArtist(artistUsername: String): Cursor =
        readableDatabase.rawQuery(
            """
        SELECT o.order_id, o.user_username, o.status, p.title
        FROM orders o
        INNER JOIN paintings p ON o.painting_id = p.painting_id
        WHERE o.artist_username=?
        ORDER BY o.created_at DESC
        """,
            arrayOf(artistUsername)
        )

    fun getOrdersForUser(userUsername: String): Cursor =
        readableDatabase.rawQuery(
            """
        SELECT o.order_id, o.status, p.title, p.image_uri, o.artist_username
        FROM orders o
        INNER JOIN paintings p ON o.painting_id = p.painting_id
        WHERE o.user_username=?
        ORDER BY o.created_at DESC
        """,
            arrayOf(userUsername)
        )

    fun updateOrderStatus(orderId: Int, status: String): Boolean =
        writableDatabase.update(
            "orders",
            ContentValues().apply { put("status", status) },
            "order_id=?",
            arrayOf(orderId.toString())
        ) > 0
    fun getOrdersForArtistDetailed(artistUsername: String): Cursor =
        readableDatabase.rawQuery(
            """
        SELECT 
            o.order_id,
            p.title,
            u.name,
            u.username,
            u.phone,
            u.address,

            o.status
        FROM orders o
        JOIN paintings p ON o.painting_id = p.painting_id
        JOIN users u ON o.user_username = u.username
        WHERE o.artist_username = ?
        ORDER BY o.created_at DESC
        """,
            arrayOf(artistUsername)
        )
    fun getUserOrders(username: String): Cursor =
        readableDatabase.rawQuery(
            """
        SELECT 
            p.image_Uri,
            p.title,
            a.name,
            a.username,
            a.phone,
            p.price,
            o.status
        FROM orders o
        INNER JOIN paintings p ON o.painting_id = p.painting_id
        INNER JOIN artists a ON o.artist_username = a.username
        WHERE o.user_username=?
        ORDER BY o.created_at DESC
        """,
            arrayOf(username)
        )
    // =========================
// ORDER DETAILS (ARTIST SIDE)
// =========================
    fun getOrderDetailsByOrderId(orderId: Int): Cursor =
        readableDatabase.rawQuery(
            """
        SELECT
            u.name,
            u.username,
            u.phone,
            u.address,
            p.title,
            p.description,
            p.price,
            p.image_uri
        FROM orders o
        INNER JOIN users u ON o.user_username = u.username
        INNER JOIN paintings p ON o.painting_id = p.painting_id
        WHERE o.order_id = ?
        """,
            arrayOf(orderId.toString())
        )
}

