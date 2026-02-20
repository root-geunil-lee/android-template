package com.example.androidtemplate

import android.net.Uri
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.browser.customtabs.CustomTabsIntent
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.androidtemplate.auth.AuthCallbackBus
import com.example.androidtemplate.core.navigation.AppRoutes
import com.example.androidtemplate.features.auth.AuthMethodsScreen
import com.example.androidtemplate.features.auth.EmailSignInScreen
import com.example.androidtemplate.features.auth.AuthRepositoryContract
import com.example.androidtemplate.features.auth.AuthResult
import com.example.androidtemplate.features.auth.OAuthFlowState
import com.example.androidtemplate.features.auth.OAuthProvider
import com.example.androidtemplate.features.auth.OAuthUseCase
import com.example.androidtemplate.features.auth.OtpAuthUseCase
import com.example.androidtemplate.features.auth.OtpFlowState
import com.example.androidtemplate.features.auth.OtpVerifyScreen
import com.example.androidtemplate.features.billing.PaywallResult
import com.example.androidtemplate.features.billing.PaywallSyncStatus
import com.example.androidtemplate.features.billing.PaywallSheetRoute
import com.example.androidtemplate.features.home.HomeScreen
import com.example.androidtemplate.features.mypage.MyPageRoute
import com.example.androidtemplate.features.mypage.PurchaseHistoryScreen
import com.example.androidtemplate.features.mypage.SubscriptionScreen
import com.example.androidtemplate.features.mypage.TransactionDetailScreen
import com.example.androidtemplate.features.mypage.EditProfileScreen
import com.example.androidtemplate.core.ui.DesignTokens
import com.example.androidtemplate.core.ui.effectiveHorizontalPaddingPx
import kotlinx.coroutines.launch

@Composable
fun AndroidTemplateApp(
  authRepository: AuthRepositoryContract,
) {
  var isAuthenticated by rememberSaveable { mutableStateOf(false) }
  if (isAuthenticated) {
    AuthenticatedApp(
      authRepository = authRepository,
      onLogout = { isAuthenticated = false },
    )
  } else {
    UnauthenticatedApp(
      authRepository = authRepository,
      onAuthenticated = { isAuthenticated = true },
    )
  }
}

@Composable
private fun UnauthenticatedApp(
  authRepository: AuthRepositoryContract,
  onAuthenticated: () -> Unit,
) {
  val context = LocalContext.current
  val horizontalPadding = rememberHorizontalContentPadding()
  val customTabsIntent = remember {
    CustomTabsIntent.Builder()
      .setShowTitle(true)
      .build()
  }
  val navController = rememberNavController()
  val coroutineScope = rememberCoroutineScope()
  val oauthUseCase = remember(authRepository) { OAuthUseCase(authRepository) }
  val otpAuthUseCase = remember(authRepository) { OtpAuthUseCase(authRepository) }
  var oauthFlowState by remember { mutableStateOf<OAuthFlowState>(OAuthFlowState.Idle) }
  var otpFlowState by remember { mutableStateOf<OtpFlowState>(OtpFlowState.Idle) }

  LaunchedEffect(authRepository) {
    AuthCallbackBus.callbacks.collect { callbackUri ->
      oauthFlowState = oauthUseCase.handleCallback(callbackUri)
      when (oauthFlowState) {
        OAuthFlowState.Authenticated -> onAuthenticated()
        is OAuthFlowState.Error -> {
          navController.navigate(AppRoutes.AUTH_METHODS) {
            popUpTo(AppRoutes.AUTH_METHODS) { inclusive = false }
            launchSingleTop = true
          }
        }
        else -> Unit
      }
    }
  }

  fun startOAuth(provider: OAuthProvider) {
    oauthFlowState = oauthUseCase.start(provider)
    val launchState = oauthFlowState as? OAuthFlowState.LaunchBrowser ?: return

    // Demo fallback repository can emit an app link directly.
    if (launchState.url.startsWith("androidtemplate://")) {
      AuthCallbackBus.emit(launchState.url)
      return
    }

    runCatching {
      customTabsIntent.launchUrl(context, Uri.parse(launchState.url))
    }.onFailure {
      oauthFlowState = OAuthFlowState.Error("Failed to launch OAuth browser")
    }
  }

  NavHost(
    navController = navController,
    startDestination = AppRoutes.AUTH_METHODS,
    modifier = Modifier
      .fillMaxSize()
      .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top))
      .imePadding()
      .padding(horizontal = horizontalPadding),
  ) {
    composable(AppRoutes.AUTH_METHODS) {
      AuthMethodsScreen(
        oauthState = oauthFlowState,
        onApple = { startOAuth(OAuthProvider.Apple) },
        onGoogle = { startOAuth(OAuthProvider.Google) },
        onKakao = { startOAuth(OAuthProvider.Kakao) },
        onContinueWithEmail = {
          oauthFlowState = OAuthFlowState.Idle
          otpFlowState = OtpFlowState.Idle
          navController.navigate(AppRoutes.AUTH_EMAIL)
        },
      )
    }

    composable(AppRoutes.AUTH_EMAIL) {
      EmailSignInScreen(
        state = otpFlowState,
        onBack = { navController.popBackStack() },
        onSendCode = { email ->
          coroutineScope.launch {
            otpFlowState = otpAuthUseCase.sendCode(email)
            if (otpFlowState is OtpFlowState.SentCode) {
              navController.navigate("auth/otp?email=${Uri.encode(email)}")
            }
          }
        },
      )
    }

    composable(
      route = AppRoutes.AUTH_OTP,
      arguments = listOf(navArgument("email") { type = NavType.StringType }),
    ) { backStackEntry ->
      val email = backStackEntry.arguments?.getString("email").orEmpty()
      OtpVerifyScreen(
        email = email,
        state = otpFlowState,
        onBack = { navController.popBackStack() },
        onVerifyCode = { code ->
          coroutineScope.launch {
            otpFlowState = otpAuthUseCase.verifyCode(email = email, code = code)
            if (otpFlowState == OtpFlowState.VerifiedSuccess) {
              onAuthenticated()
            }
          }
        },
      )
    }
  }
}

@Composable
private fun AuthenticatedApp(
  authRepository: AuthRepositoryContract,
  onLogout: () -> Unit,
) {
  val navController = rememberNavController()
  val coroutineScope = rememberCoroutineScope()
  val snackbarHostState = remember { SnackbarHostState() }
  val horizontalPadding = rememberHorizontalContentPadding()
  var paywallResultMessage by rememberSaveable { mutableStateOf<String?>(null) }

  val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
  val isBottomBarVisible = currentRoute == AppRoutes.HOME || currentRoute == AppRoutes.MYPAGE

  Scaffold(
    modifier = Modifier
      .fillMaxSize()
      .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top))
      .imePadding(),
    snackbarHost = { SnackbarHost(snackbarHostState) },
    bottomBar = {
      if (isBottomBarVisible) {
        NavigationBar {
          NavigationBarItem(
            selected = currentRoute == AppRoutes.HOME,
            onClick = { navController.navigate(AppRoutes.HOME) },
            label = { Text("Home") },
            icon = { Text("H") },
          )
          NavigationBarItem(
            selected = currentRoute == AppRoutes.MYPAGE,
            onClick = { navController.navigate(AppRoutes.MYPAGE) },
            label = { Text("My Page") },
            icon = { Text("M") },
          )
        }
      }
    },
  ) { innerPadding ->
    NavHost(
      navController = navController,
      startDestination = AppRoutes.HOME,
      modifier = Modifier
        .padding(innerPadding)
        .padding(horizontal = horizontalPadding),
    ) {
      composable(AppRoutes.HOME) {
        HomeScreen(
          onOpenPaywall = { navController.navigate(AppRoutes.PAYWALL) },
          paywallResultMessage = paywallResultMessage,
        )
      }

      composable(AppRoutes.PAYWALL) {
        PaywallSheetRoute(
          onClose = { navController.popBackStack() },
          onResult = { result ->
            paywallResultMessage = when (result) {
              is PaywallResult.Purchased -> {
                if (result.syncStatus == PaywallSyncStatus.Synced) {
                  "Purchased: ${result.productId}"
                } else {
                  "Purchased: ${result.productId} (sync failed)"
                }
              }
              is PaywallResult.Restored -> {
                if (result.syncStatus == PaywallSyncStatus.Synced) {
                  "Restored ${result.count} purchase(s)"
                } else {
                  "Restored ${result.count} purchase(s) (sync failed)"
                }
              }
              PaywallResult.Cancelled -> "Purchase cancelled"
              PaywallResult.Pending -> "Purchase pending"
              is PaywallResult.Failed -> result.message
            }
          },
        )
      }

      composable(AppRoutes.MYPAGE) {
        MyPageRoute(
          onEditProfile = { navController.navigate(AppRoutes.MYPAGE_EDIT_PROFILE) },
          onSubscription = { navController.navigate(AppRoutes.MYPAGE_SUBSCRIPTION) },
          onPurchaseHistory = { navController.navigate(AppRoutes.MYPAGE_PURCHASE_HISTORY) },
          onLogoutCompleted = {
            coroutineScope.launch {
              when (val result = authRepository.logout()) {
                AuthResult.Success -> onLogout()
                is AuthResult.RateLimited -> snackbarHostState.showSnackbar("Too many requests. Retry in ${result.retryAfterSeconds}s")
                is AuthResult.Failure -> snackbarHostState.showSnackbar(result.message)
              }
            }
          },
          onDeleteCompleted = {
            coroutineScope.launch {
              when (val result = authRepository.clearLocalSession()) {
                AuthResult.Success -> onLogout()
                is AuthResult.RateLimited -> snackbarHostState.showSnackbar("Too many requests. Retry in ${result.retryAfterSeconds}s")
                is AuthResult.Failure -> snackbarHostState.showSnackbar(result.message)
              }
            }
          },
        )
      }

      composable(AppRoutes.MYPAGE_EDIT_PROFILE) {
        EditProfileScreen(
          onBack = { navController.popBackStack() },
        )
      }

      composable(AppRoutes.MYPAGE_SUBSCRIPTION) {
        SubscriptionScreen(
          onBack = { navController.popBackStack() },
        )
      }

      composable(AppRoutes.MYPAGE_PURCHASE_HISTORY) {
        PurchaseHistoryScreen(
          onBack = { navController.popBackStack() },
          onOpenTransaction = { transactionId ->
            navController.navigate("mypage/transaction/$transactionId")
          },
        )
      }

      composable(
        route = AppRoutes.MYPAGE_TRANSACTION_DETAIL,
        arguments = listOf(navArgument("id") { type = NavType.StringType }),
      ) { backStackEntry ->
        val transactionId = backStackEntry.arguments?.getString("id").orEmpty()
        TransactionDetailScreen(
          transactionId = transactionId,
          onBack = { navController.popBackStack() },
        )
      }
    }
  }

  if (currentRoute !in listOf(AppRoutes.HOME, AppRoutes.MYPAGE, AppRoutes.MYPAGE_EDIT_PROFILE, AppRoutes.MYPAGE_SUBSCRIPTION, AppRoutes.MYPAGE_PURCHASE_HISTORY, AppRoutes.MYPAGE_TRANSACTION_DETAIL, AppRoutes.PAYWALL)) {
    Button(onClick = { navController.navigate(AppRoutes.HOME) }) {
      Text("Go Home")
    }
  }

}

@Composable
private fun rememberHorizontalContentPadding() = with(LocalDensity.current) {
  val layoutDirection = LocalLayoutDirection.current
  val baselinePx = DesignTokens.Spacing.BaseHorizontalDp.dp.roundToPx()
  val safeInsetLeftPx = WindowInsets.safeDrawing.getLeft(this, layoutDirection)
  val safeInsetRightPx = WindowInsets.safeDrawing.getRight(this, layoutDirection)
  effectiveHorizontalPaddingPx(
    baselinePx = baselinePx,
    safeInsetLeftPx = safeInsetLeftPx,
    safeInsetRightPx = safeInsetRightPx,
  ).toDp()
}
