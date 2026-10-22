package com.example.kalasetu

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    companion object {
        private const val PREF_NAME = "kalasetu_session"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
        private const val KEY_ROLE = "role"
        private const val KEY_USERNAME = "username"
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    private val editor: SharedPreferences.Editor = prefs.edit()

    // ======================
    // SAVE LOGIN SESSION
    // ======================
    fun saveLogin(role: String, username: String) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.putString(KEY_ROLE, role)
        editor.putString(KEY_USERNAME, username)
        editor.apply()
    }

    // ======================
    // CHECK LOGIN STATE
    // ======================
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    // ======================
    // GET ROLE
    // ======================
    fun getRole(): String {
        return prefs.getString(KEY_ROLE, "") ?: ""
    }

    // ======================
    // GET USERNAME
    // ======================
    fun getUsername(): String {
        return prefs.getString(KEY_USERNAME, "") ?: ""
    }

    // ======================
    // LOGOUT (CLEAR SESSION)
    // ======================
    fun logout() {
        editor.clear()
        editor.apply()
    }
}
