package com.example.androidtemplate.features.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun AuthMethodsScreen(
  onApple: () -> Unit,
  onGoogle: () -> Unit,
  onKakao: () -> Unit,
  onContinueWithEmail: () -> Unit,
) {
  Column(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.Center,
  ) {
    Text("Sign in", style = MaterialTheme.typography.headlineMedium)
    Spacer(Modifier.height(24.dp))
    Button(onClick = onApple, modifier = Modifier.fillMaxWidth()) { Text("Continue with Apple") }
    Spacer(Modifier.height(12.dp))
    Button(onClick = onGoogle, modifier = Modifier.fillMaxWidth()) { Text("Continue with Google") }
    Spacer(Modifier.height(12.dp))
    Button(onClick = onKakao, modifier = Modifier.fillMaxWidth()) { Text("Continue with Kakao") }
    Spacer(Modifier.height(12.dp))
    Button(onClick = onContinueWithEmail, modifier = Modifier.fillMaxWidth()) { Text("Continue with Email") }
  }
}

@Composable
fun EmailSignInScreen(
  onBack: () -> Unit,
  onSendCode: (String) -> Unit,
) {
  var email by rememberSaveable { mutableStateOf("") }
  val isValid = email.contains("@") && email.contains(".")

  Column(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.Center,
  ) {
    Text("Email Sign In", style = MaterialTheme.typography.titleLarge)
    Spacer(Modifier.height(16.dp))
    OutlinedTextField(
      value = email,
      onValueChange = { email = it.trim() },
      label = { Text("Email") },
      modifier = Modifier
        .fillMaxWidth()
        .testTag("email_input"),
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
      isError = email.isNotEmpty() && !isValid,
      supportingText = {
        if (email.isNotEmpty() && !isValid) {
          Text("Please enter a valid email")
        }
      },
    )

    Spacer(Modifier.height(12.dp))
    Button(
      onClick = { onSendCode(email) },
      enabled = isValid,
      modifier = Modifier.fillMaxWidth(),
    ) {
      Text("Send verification code")
    }

    Spacer(Modifier.height(8.dp))
    Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Back") }
  }
}

@Composable
fun OtpVerifyScreen(
  email: String,
  onBack: () -> Unit,
  onVerified: () -> Unit,
) {
  var otp by rememberSaveable { mutableStateOf("") }

  LaunchedEffect(otp) {
    if (otp.length == 6) {
      delay(300)
      onVerified()
    }
  }

  Column(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.Center,
  ) {
    Text("Verify OTP", style = MaterialTheme.typography.titleLarge)
    Text(email)
    Spacer(Modifier.height(16.dp))
    OutlinedTextField(
      value = otp,
      onValueChange = {
        otp = it.filter { char -> char.isDigit() }.take(6)
      },
      label = { Text("6-digit code") },
      modifier = Modifier.fillMaxWidth(),
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
      supportingText = { Text("Auto-verify triggers at 6 digits") },
    )

    Spacer(Modifier.height(12.dp))
    Button(onClick = onVerified, enabled = otp.length == 6, modifier = Modifier.fillMaxWidth()) {
      Text("Verify")
    }

    Spacer(Modifier.height(8.dp))
    Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Back") }
  }
}
