package com.example.kalasetu

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat

class SplashActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())
    private val splashDelay = 2500L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ SAFE full-screen handling (no deprecated flags)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_splash)

        val imgLogo = findViewById<ImageView>(R.id.imgLogo)
        val animation = AnimationUtils.loadAnimation(this, R.anim.fade_scale)
        imgLogo.startAnimation(animation)

        val session = SessionManager(this)

        handler.postDelayed({
            val intent = when {
                session.isLoggedIn() && session.getRole() == "USER" ->
                    Intent(this, UserDashboardActivity::class.java)
                        .putExtra("USERNAME", session.getUsername())

                session.isLoggedIn() && session.getRole() == "ARTIST" ->
                    Intent(this, ArtistDashboardActivity::class.java)
                        .putExtra("USERNAME", session.getUsername())

                session.isLoggedIn() && session.getRole() == "ADMIN" ->
                    Intent(this, AdminDashboardActivity::class.java)

                else ->
                    Intent(this, RoleSelectionActivity::class.java)
            }

            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }, splashDelay)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null) // ✅ prevent leaks
    }
}
