package com.example.androidtemplate.di

import android.content.Context
import com.example.androidtemplate.BuildConfig
import com.example.androidtemplate.auth.EncryptedSessionStore
import com.example.androidtemplate.core.storage.SessionStore
import com.example.androidtemplate.features.auth.AuthRepository
import com.example.androidtemplate.features.auth.AuthRepositoryContract
import com.example.androidtemplate.features.auth.ConfigurableAuthRepository
import com.example.androidtemplate.features.auth.DemoAuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

  @Provides
  @Singleton
  fun provideSessionStore(
    @ApplicationContext context: Context,
  ): SessionStore = EncryptedSessionStore(context)

  @Provides
  @Singleton
  fun provideAuthRepository(
    sessionStore: SessionStore,
  ): AuthRepositoryContract {
    val baseUrl = BuildConfig.AUTH_BASE_URL
    val redirectUrl = BuildConfig.SUPABASE_REDIRECT_URL

    return ConfigurableAuthRepository(
      authBaseUrl = baseUrl,
      fallbackRepository = DemoAuthRepository(),
      remoteRepositoryFactory = {
        AuthRepository(
          baseUrl = baseUrl,
          redirectUrl = redirectUrl,
          sessionStore = sessionStore,
        )
      },
    )
  }
}
