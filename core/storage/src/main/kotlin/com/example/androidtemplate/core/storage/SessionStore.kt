package com.example.androidtemplate.core.storage

interface SessionStore {
  suspend fun accessToken(): String?
  suspend fun clearSession()
}
