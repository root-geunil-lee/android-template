package com.example.androidtemplate.auth

import android.content.Context
import com.example.androidtemplate.BuildConfig
import com.example.androidtemplate.features.auth.AuthRepository
import com.example.androidtemplate.features.auth.AuthRepositoryContract
import com.example.androidtemplate.features.auth.ConfigurableAuthRepository
import com.example.androidtemplate.features.auth.DemoAuthRepository

fun provideAuthRepository(context: Context): AuthRepositoryContract {
  val baseUrl = BuildConfig.AUTH_BASE_URL
  val redirectUrl = BuildConfig.SUPABASE_REDIRECT_URL

  return ConfigurableAuthRepository(
    authBaseUrl = baseUrl,
    fallbackRepository = DemoAuthRepository(),
    remoteRepositoryFactory = {
      AuthRepository(
        baseUrl = baseUrl,
        redirectUrl = redirectUrl,
        sessionStore = EncryptedSessionStore(context),
      )
    },
  )
}
