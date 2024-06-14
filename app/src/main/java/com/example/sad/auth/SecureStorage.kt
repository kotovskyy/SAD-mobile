package com.example.sad.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

object SecureStorage {

    private const val PREFS_FILENAME = "secure_prefs"
    private const val TOKEN_KEY = "api_token"

    private fun getEncryptedSharedPreferences(context: Context): SharedPreferences {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        return EncryptedSharedPreferences.create(
            PREFS_FILENAME,
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun storeToken(context: Context, token: String) {
        val encryptedPrefs = getEncryptedSharedPreferences(context)
        encryptedPrefs.edit().putString(TOKEN_KEY, token).apply()
    }

    fun getToken(context: Context): String? {
        val encryptedPrefs = getEncryptedSharedPreferences(context)
        return encryptedPrefs.getString(TOKEN_KEY, null)
    }

    fun clearToken(context: Context) {
        val encryptedPrefs = getEncryptedSharedPreferences(context)
        encryptedPrefs.edit().remove(TOKEN_KEY).apply()
    }
}
