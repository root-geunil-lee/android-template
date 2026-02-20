package com.example.androidtemplate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.androidtemplate.auth.AuthCallbackBus
import com.example.androidtemplate.ui.theme.AndroidTemplateTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    intent?.dataString?.let(AuthCallbackBus::emit)
    setContent {
      AndroidTemplateTheme {
        AndroidTemplateApp()
      }
    }
  }

  override fun onNewIntent(intent: android.content.Intent) {
    super.onNewIntent(intent)
    intent.dataString?.let(AuthCallbackBus::emit)
  }
}
