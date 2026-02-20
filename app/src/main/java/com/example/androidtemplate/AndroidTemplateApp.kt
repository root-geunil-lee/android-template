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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.androidtemplate.core.navigation.AppRoutes
import com.example.androidtemplate.features.auth.AuthMethodsScreen
import com.example.androidtemplate.features.auth.DemoAuthRepository
import com.example.androidtemplate.features.auth.EmailSignInScreen
import com.example.androidtemplate.features.auth.OtpAuthUseCase
import com.example.androidtemplate.features.auth.OtpFlowState
import com.example.androidtemplate.features.auth.OtpVerifyScreen
import com.example.androidtemplate.features.billing.PaywallResult
import com.example.androidtemplate.features.billing.PaywallSheetRoute
import com.example.androidtemplate.features.home.HomeScreen
import com.example.androidtemplate.features.mypage.MyPageRoute
import com.example.androidtemplate.features.mypage.PurchaseHistoryScreen
import com.example.androidtemplate.features.mypage.SubscriptionScreen
import com.example.androidtemplate.features.mypage.TransactionDetailScreen
import com.example.androidtemplate.features.mypage.EditProfileScreen
import kotlinx.coroutines.launch

@Composable
fun AndroidTemplateApp() {
  var isAuthenticated by rememberSaveable { mutableStateOf(false) }
  if (isAuthenticated) {
    AuthenticatedApp(onLogout = { isAuthenticated = false })
  } else {
    UnauthenticatedApp(onAuthenticated = { isAuthenticated = true })
  }
}

@Composable
private fun UnauthenticatedApp(onAuthenticated: () -> Unit) {
  val navController = rememberNavController()
  val coroutineScope = rememberCoroutineScope()
  val otpAuthUseCase = remember { OtpAuthUseCase(DemoAuthRepository()) }
  var otpFlowState by remember { mutableStateOf<OtpFlowState>(OtpFlowState.Idle) }

  NavHost(
    navController = navController,
    startDestination = AppRoutes.AUTH_METHODS,
  ) {
    composable(AppRoutes.AUTH_METHODS) {
      AuthMethodsScreen(
        onApple = onAuthenticated,
        onGoogle = onAuthenticated,
        onKakao = onAuthenticated,
        onContinueWithEmail = {
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
private fun AuthenticatedApp(onLogout: () -> Unit) {
  val navController = rememberNavController()
  val snackbarHostState = remember { SnackbarHostState() }
  var paywallResultMessage by rememberSaveable { mutableStateOf<String?>(null) }

  val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
  val isBottomBarVisible = currentRoute == AppRoutes.HOME || currentRoute == AppRoutes.MYPAGE

  Scaffold(
    modifier = Modifier
      .fillMaxSize()
      .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal))
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
        .padding(horizontal = 20.dp),
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
              is PaywallResult.Purchased -> "Purchased: ${result.productId}"
              is PaywallResult.Restored -> "Restored ${result.count} purchase(s)"
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
          onLogoutCompleted = onLogout,
          onDeleteCompleted = onLogout,
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
