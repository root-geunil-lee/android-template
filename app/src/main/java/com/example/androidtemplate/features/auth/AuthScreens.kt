package com.example.androidtemplate.features.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun AuthMethodsScreen(
  oauthState: OAuthFlowState,
  onApple: () -> Unit,
  onGoogle: () -> Unit,
  onKakao: () -> Unit,
  onContinueWithEmail: () -> Unit,
) {
  val oauthMessage = when (oauthState) {
    is OAuthFlowState.Error -> oauthState.message
    OAuthFlowState.HandlingCallback -> "Signing in..."
    else -> null
  }
  val isBusy = oauthState == OAuthFlowState.HandlingCallback

  Column(
    modifier = Modifier
      .fillMaxSize()
      .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom))
      .verticalScroll(rememberScrollState())
      // Auth graph host already applies status bar insets; keep +32dp breathing room per spec.
      .padding(top = 32.dp)
      .testTag("auth_methods_scroll"),
    verticalArrangement = Arrangement.Top,
  ) {
    Text("Sign in", style = MaterialTheme.typography.headlineMedium)
    if (!oauthMessage.isNullOrBlank()) {
      Spacer(Modifier.height(8.dp))
      Text(
        text = oauthMessage,
        color = MaterialTheme.colorScheme.error,
      )
    }
    Spacer(Modifier.height(24.dp))
    // Keep iOS parity for copy/entry points while using Android-native M3 hierarchy.
    // Google is the single primary action, Apple is secondary outline, Kakao is tertiary tonal,
    // and Email stays lightweight as text action.
    Column(
      verticalArrangement = Arrangement.spacedBy(16.dp),
      modifier = Modifier.fillMaxWidth(),
    ) {
      Button(
        onClick = onGoogle,
        enabled = !isBusy,
        shape = RoundedCornerShape(24.dp),
        elevation = ButtonDefaults.buttonElevation(
          defaultElevation = 1.dp,
          pressedElevation = 1.dp,
          focusedElevation = 1.dp,
          hoveredElevation = 1.dp,
          disabledElevation = 0.dp,
        ),
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primary,
          contentColor = MaterialTheme.colorScheme.onPrimary,
          disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
          disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(min = 56.dp)
          .semantics { contentDescription = "Continue with Google" }
          .testTag("auth_provider_0_google"),
      ) {
        AuthProviderLabel(
          badge = "G",
          text = "Continue with Google",
          badgeContainerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.16f),
          badgeContentColor = MaterialTheme.colorScheme.onPrimary,
        )
      }

      OutlinedButton(
        onClick = onApple,
        enabled = !isBusy,
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = ButtonDefaults.buttonElevation(
          defaultElevation = 0.dp,
          pressedElevation = 0.dp,
          focusedElevation = 0.dp,
          hoveredElevation = 0.dp,
          disabledElevation = 0.dp,
        ),
        colors = ButtonDefaults.outlinedButtonColors(
          contentColor = MaterialTheme.colorScheme.onSurface,
          disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(min = 56.dp)
          .semantics { contentDescription = "Continue with Apple" }
          .testTag("auth_provider_1_apple"),
      ) {
        AuthProviderLabel(
          badge = "A",
          text = "Continue with Apple",
          badgeContainerColor = MaterialTheme.colorScheme.surfaceVariant,
          badgeContentColor = MaterialTheme.colorScheme.onSurface,
        )
      }

      FilledTonalButton(
        onClick = onKakao,
        enabled = !isBusy,
        shape = RoundedCornerShape(24.dp),
        elevation = ButtonDefaults.buttonElevation(
          defaultElevation = 0.dp,
          pressedElevation = 0.dp,
          focusedElevation = 0.dp,
          hoveredElevation = 0.dp,
          disabledElevation = 0.dp,
        ),
        colors = ButtonDefaults.filledTonalButtonColors(
          containerColor = MaterialTheme.colorScheme.surfaceVariant,
          contentColor = MaterialTheme.colorScheme.onSurface,
          disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
          disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(min = 56.dp)
          .semantics { contentDescription = "Continue with Kakao" }
          .testTag("auth_provider_2_kakao"),
      ) {
        AuthProviderLabel(
          badge = "K",
          text = "Continue with Kakao",
          badgeContainerColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
          badgeContentColor = MaterialTheme.colorScheme.onSurface,
        )
      }

      TextButton(
        onClick = onContinueWithEmail,
        enabled = !isBusy,
        shape = RoundedCornerShape(24.dp),
        elevation = ButtonDefaults.buttonElevation(
          defaultElevation = 0.dp,
          pressedElevation = 0.dp,
          focusedElevation = 0.dp,
          hoveredElevation = 0.dp,
          disabledElevation = 0.dp,
        ),
        colors = ButtonDefaults.textButtonColors(
          contentColor = MaterialTheme.colorScheme.onSurface,
          disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(min = 56.dp)
          .semantics { contentDescription = "Continue with Email" }
          .testTag("auth_provider_3_email"),
      ) {
        AuthProviderLabel(
          badge = "@",
          text = "Continue with Email",
          badgeContainerColor = MaterialTheme.colorScheme.surfaceVariant,
          badgeContentColor = MaterialTheme.colorScheme.onSurface,
        )
      }
    }
  }
}

@Composable
private fun AuthProviderLabel(
  badge: String,
  text: String,
  badgeContainerColor: androidx.compose.ui.graphics.Color,
  badgeContentColor: androidx.compose.ui.graphics.Color,
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(12.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Box(
      modifier = Modifier
        .size(24.dp)
        .background(color = badgeContainerColor, shape = RoundedCornerShape(12.dp)),
      contentAlignment = Alignment.Center,
    ) {
      Text(
        text = badge,
        color = badgeContentColor,
        style = MaterialTheme.typography.labelSmall,
      )
    }
    Text(
      text = text,
      style = MaterialTheme.typography.bodyLarge,
      modifier = Modifier.weight(1f),
      textAlign = TextAlign.Start,
    )
  }
}

@Composable
fun EmailSignInScreen(
  state: OtpFlowState,
  onBack: () -> Unit,
  onSendCode: (String) -> Unit,
) {
  var email by rememberSaveable { mutableStateOf("") }
  val isValid = email.contains("@") && email.contains(".")

  val backendMessage = when (state) {
    is OtpFlowState.Error -> state.message
    is OtpFlowState.RateLimited -> "Too many requests. Try again in ${state.retryAfterSeconds}s"
    else -> null
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .testTag("email_sign_in_scroll"),
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
      isError = (email.isNotEmpty() && !isValid) || backendMessage != null,
      supportingText = {
        when {
          email.isNotEmpty() && !isValid -> Text("Please enter a valid email")
          backendMessage != null -> Text(backendMessage)
        }
      },
    )

    Spacer(Modifier.height(12.dp))
    Button(
      onClick = { onSendCode(email) },
      enabled = isValid && state != OtpFlowState.SendingCode,
      modifier = Modifier.fillMaxWidth(),
    ) {
      Text(if (state == OtpFlowState.SendingCode) "Sending..." else "Send verification code")
    }

    Spacer(Modifier.height(8.dp))
    Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Back") }
  }
}

@Composable
fun OtpVerifyScreen(
  email: String,
  state: OtpFlowState,
  onBack: () -> Unit,
  onResendCode: () -> Unit,
  onVerifyCode: (String) -> Unit,
) {
  var otp by rememberSaveable { mutableStateOf("") }
  var lastAutoSubmittedCode by rememberSaveable { mutableStateOf("") }
  var resendCooldownSeconds by rememberSaveable(email) { mutableStateOf(0) }
  val focusRequester = remember { FocusRequester() }

  val backendMessage = when (state) {
    is OtpFlowState.Error -> state.message
    is OtpFlowState.RateLimited -> "Too many attempts. Try again in ${state.retryAfterSeconds}s"
    else -> null
  }

  LaunchedEffect(Unit) {
    focusRequester.requestFocus()
  }

  LaunchedEffect(state, email) {
    when (state) {
      is OtpFlowState.SentCode -> {
        if (state.email == email) {
          resendCooldownSeconds = state.cooldownSeconds
        }
      }
      is OtpFlowState.RateLimited -> {
        resendCooldownSeconds = maxOf(resendCooldownSeconds, state.retryAfterSeconds)
      }
      else -> Unit
    }
  }

  LaunchedEffect(resendCooldownSeconds) {
    if (resendCooldownSeconds > 0) {
      delay(1000)
      resendCooldownSeconds -= 1
    }
  }

  LaunchedEffect(otp, state) {
    if (otp.length == OTP_CODE_LENGTH && otp != lastAutoSubmittedCode && state != OtpFlowState.VerifyingCode) {
      lastAutoSubmittedCode = otp
      onVerifyCode(otp)
    }
  }

  val canResend = resendCooldownSeconds == 0 &&
    state != OtpFlowState.SendingCode &&
    state != OtpFlowState.VerifyingCode
  val resendStatusText = when {
    resendCooldownSeconds <= 0 -> null
    state is OtpFlowState.RateLimited -> "Try again in ${formatCooldownMmSs(resendCooldownSeconds)}."
    else -> "Resend in ${formatCooldownMmSs(resendCooldownSeconds)}"
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .testTag("otp_verify_scroll"),
    verticalArrangement = Arrangement.Center,
  ) {
    Text("Verify OTP", style = MaterialTheme.typography.titleLarge)
    Text(email)
    Spacer(Modifier.height(16.dp))
    BasicTextField(
      value = otp,
      onValueChange = {
        otp = sanitizeOtpInput(it)
      },
      singleLine = true,
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
      modifier = Modifier
        .focusRequester(focusRequester)
        .size(1.dp)
        .alpha(0f)
        .testTag("otp_hidden_input"),
    )
    OtpDigitSlots(
      otp = otp,
      focusRequester = focusRequester,
    )
    Spacer(Modifier.height(8.dp))
    Text(
      text = backendMessage ?: "Auto-verify triggers at 6 digits",
      color = if (backendMessage != null) {
        MaterialTheme.colorScheme.error
      } else {
        MaterialTheme.colorScheme.onSurfaceVariant
      },
    )

    Spacer(Modifier.height(12.dp))
    Button(
      onClick = { onVerifyCode(otp) },
      enabled = otp.length == OTP_CODE_LENGTH && state != OtpFlowState.VerifyingCode,
      modifier = Modifier.fillMaxWidth(),
    ) {
      Text(if (state == OtpFlowState.VerifyingCode) "Verifying..." else "Verify")
    }

    Spacer(Modifier.height(8.dp))
    Button(
      onClick = onResendCode,
      enabled = canResend,
      modifier = Modifier.fillMaxWidth(),
    ) {
      Text("Resend code")
    }

    if (!resendStatusText.isNullOrBlank()) {
      Spacer(Modifier.height(8.dp))
      Text(
        text = resendStatusText,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }

    Spacer(Modifier.height(8.dp))
    Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Back") }
  }
}

@Composable
private fun OtpDigitSlots(
  otp: String,
  focusRequester: FocusRequester,
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    repeat(OTP_CODE_LENGTH) { index ->
      val digit = otp.getOrNull(index)?.toString().orEmpty()
      Box(
        modifier = Modifier
          .size(52.dp)
          .border(
            width = 1.dp,
            color = if (digit.isBlank()) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(12.dp),
          )
          .background(
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(12.dp),
          )
          .clickable(onClick = { focusRequester.requestFocus() })
          .semantics { contentDescription = otpSlotDescription(index) }
          .testTag("otp_digit_slot_$index"),
        contentAlignment = Alignment.Center,
      ) {
        Text(
          text = if (digit.isBlank()) " " else digit,
          style = MaterialTheme.typography.titleLarge,
          textAlign = TextAlign.Center,
        )
      }
    }
  }
}
