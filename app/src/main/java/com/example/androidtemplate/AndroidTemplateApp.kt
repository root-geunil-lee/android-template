package com.example.androidtemplate

import androidx.activity.compose.BackHandler
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
import com.example.androidtemplate.features.auth.EmailSignInScreen
import com.example.androidtemplate.features.auth.OtpVerifyScreen
import com.example.androidtemplate.features.billing.PaywallSheetRoute
import com.example.androidtemplate.features.home.HomeScreen
import com.example.androidtemplate.features.mypage.MyPageRoute
import com.example.androidtemplate.features.mypage.PurchaseHistoryScreen
import com.example.androidtemplate.features.mypage.SubscriptionScreen

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

  NavHost(
    navController = navController,
    startDestination = AppRoutes.AUTH_METHODS,
  ) {
    composable(AppRoutes.AUTH_METHODS) {
      AuthMethodsScreen(
        onApple = onAuthenticated,
        onGoogle = onAuthenticated,
        onKakao = onAuthenticated,
        onContinueWithEmail = { navController.navigate(AppRoutes.AUTH_EMAIL) },
      )
    }

    composable(AppRoutes.AUTH_EMAIL) {
      EmailSignInScreen(
        onBack = { navController.popBackStack() },
        onSendCode = { email ->
          navController.navigate("auth/otp?email=$email")
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
        onBack = { navController.popBackStack() },
        onVerified = onAuthenticated,
      )
    }
  }
}

@Composable
private fun AuthenticatedApp(onLogout: () -> Unit) {
  val navController = rememberNavController()
  val snackbarHostState = remember { SnackbarHostState() }

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
        )
      }

      composable(AppRoutes.PAYWALL) {
        PaywallSheetRoute(
          onClose = { navController.popBackStack() },
        )
      }

      composable(AppRoutes.MYPAGE) {
        MyPageRoute(
          onSubscription = { navController.navigate(AppRoutes.MYPAGE_SUBSCRIPTION) },
          onPurchaseHistory = { navController.navigate(AppRoutes.MYPAGE_PURCHASE_HISTORY) },
          onLogoutCompleted = onLogout,
          onDeleteCompleted = onLogout,
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
        )
      }
    }
  }

  if (currentRoute !in listOf(AppRoutes.HOME, AppRoutes.MYPAGE, AppRoutes.MYPAGE_SUBSCRIPTION, AppRoutes.MYPAGE_PURCHASE_HISTORY, AppRoutes.PAYWALL)) {
    Button(onClick = { navController.navigate(AppRoutes.HOME) }) {
      Text("Go Home")
    }
  }

  BackHandler(enabled = currentRoute == AppRoutes.PAYWALL) {
    navController.popBackStack()
  }
}
