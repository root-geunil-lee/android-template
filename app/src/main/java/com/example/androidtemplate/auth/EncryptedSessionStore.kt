package com.example.androidtemplate.auth

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.androidtemplate.core.storage.SessionStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EncryptedSessionStore(
  context: Context,
) : SessionStore {
  private val appContext = context.applicationContext

  private val encryptedPreferences by lazy {
    val masterKey = MasterKey.Builder(appContext)
      .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
      .build()

    EncryptedSharedPreferences.create(
      appContext,
      PREF_NAME,
      masterKey,
      EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
      EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )
  }

  override suspend fun accessToken(): String? = withContext(Dispatchers.IO) {
    encryptedPreferences.getString(KEY_ACCESS_TOKEN, null)
  }

  override suspend fun saveAccessToken(token: String) {
    withContext(Dispatchers.IO) {
      encryptedPreferences.edit().putString(KEY_ACCESS_TOKEN, token).apply()
    }
  }

  override suspend fun clearSession() {
    withContext(Dispatchers.IO) {
      encryptedPreferences.edit()
        .remove(KEY_ACCESS_TOKEN)
        .remove(KEY_REFRESH_TOKEN)
        .remove(KEY_USER_ID)
        .apply()
    }
  }

  private companion object {
    const val PREF_NAME = "secure_session"
    const val KEY_ACCESS_TOKEN = "access_token"
    const val KEY_REFRESH_TOKEN = "refresh_token"
    const val KEY_USER_ID = "user_id"
  }
}
