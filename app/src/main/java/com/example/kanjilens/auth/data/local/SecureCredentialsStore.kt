package com.example.kanjilens.auth.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
data class SavedCredentials(
    val email: String,
    val password: String
)

class SecureCredentialsStore(
    context: Context
) {

    companion object {
        private const val FILE_NAME = "secure_credentials"

        private const val KEY_EMAIL = "email"
        private const val KEY_PASSWORD = "password"
        private const val KEY_BIOMETRIC_DECLINED = "biometric_declined"
    }

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val preferences = EncryptedSharedPreferences.create(
        context,
        FILE_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveCredentials(
        email: String,
        password: String
    ) {
        preferences.edit()
            .putString(KEY_EMAIL, email)
            .putString(KEY_PASSWORD, password)
            .apply()
    }
    fun getCredentials(): SavedCredentials? {

        val email = getEmail()
        val password = getPassword()

        if (email == null || password == null) {
            return null
        }

        return SavedCredentials(
            email = email,
            password = password
        )
    }
    fun getEmail(): String? {
        return preferences.getString(KEY_EMAIL, null)
    }

    fun getPassword(): String? {
        return preferences.getString(KEY_PASSWORD, null)
    }

    fun hasCredentials(): Boolean {
        return getEmail() != null && getPassword() != null
    }

    fun clearCredentials() {
        preferences.edit()
            .clear()
            .apply()
    }
    fun setBiometricDeclined(declined: Boolean) {
        preferences.edit().putBoolean(KEY_BIOMETRIC_DECLINED, declined).apply()
    }

    fun isBiometricDeclined(): Boolean {
        return preferences.getBoolean(KEY_BIOMETRIC_DECLINED, false)
    }

}