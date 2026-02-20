package com.example.androidtemplate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.androidtemplate.auth.AuthCallbackBus
import com.example.androidtemplate.features.auth.AuthRepositoryContract
import com.example.androidtemplate.ui.theme.AndroidTemplateTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  private val appStateViewModel by viewModels<AppStateViewModel>()

  @Inject
  lateinit var authRepository: AuthRepositoryContract

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    intent?.dataString?.let(AuthCallbackBus::emit)
    setContent {
      AndroidTemplateTheme {
        AndroidTemplateApp(
          authRepository = authRepository,
          appStateViewModel = appStateViewModel,
        )
      }
    }
  }

  override fun onNewIntent(intent: android.content.Intent) {
    super.onNewIntent(intent)
    intent.dataString?.let(AuthCallbackBus::emit)
  }
}
