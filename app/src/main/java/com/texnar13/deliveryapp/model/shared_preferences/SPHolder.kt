package com.texnar13.deliveryapp.model.shared_preferences

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences


class SPHolder(
        private val context: Context
) {

    companion object {
        private const val PREFS_NAME = "DELIVERY_PREFS"

        // Аутентификация
        private const val AUTH_LOGIN = "AUTH_LOGIN"
        private const val AUTH_PASSWORD = "AUTH_PASSWORD"

        // Адрес сервера
        private const val SERVER_ADDRESS = "SERVER_ADDRESS"
    }

    private val preferences: SharedPreferences =
            context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

// ------------------------------------ Аутентификация ------------------------------------

    fun setUserLastAuth(login: String, pass: String) {
        val editor = preferences.edit()
        editor.putString(AUTH_LOGIN, login)
        editor.putString(AUTH_PASSWORD, pass)
        editor.apply()
    }

    fun getUserLastAuth(): Array<String> {
        return arrayOf(
            preferences.getString(AUTH_LOGIN, "")!!,
            preferences.getString(AUTH_PASSWORD, "")!!
        )
    }

// ------------------------------------ Адрес сервера ------------------------------------

    fun setServerAddress(address: String) {
        val editor = preferences.edit()
        editor.putString(SERVER_ADDRESS, address)
        editor.apply()
    }

    fun getServerAddress(): String {
        return preferences.getString(SERVER_ADDRESS, "192.168.1.66:8080")!!
    }


}