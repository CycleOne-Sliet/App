package com.cycleone.cycleoneapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.cycleone.cycleoneapp.services.NavProvider
import com.cycleone.cycleoneapp.ui.screens.AllLocations
import com.cycleone.cycleoneapp.ui.screens.ForgotPassword
import com.cycleone.cycleoneapp.ui.screens.Home
import com.cycleone.cycleoneapp.ui.screens.Landing
import com.cycleone.cycleoneapp.ui.screens.OtpScreen
import com.cycleone.cycleoneapp.ui.screens.SignIn
import com.cycleone.cycleoneapp.ui.screens.SignUp
import com.cycleone.cycleoneapp.ui.screens.UnlockScreen
import com.cycleone.cycleoneapp.ui.theme.CycleoneAppTheme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.serialization.Serializable

@Serializable
class SignInPath

val uri = "cycleone://cycleone.base"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CycleoneAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BaseController()
                }
            }
        }
    }
}

@Composable
fun BaseController(navController: NavHostController = rememberNavController()) {
    NavProvider.controller = navController
    val user = FirebaseAuth.getInstance().currentUser
    val currentLocation = if (user == null) {
        "/landing"
    } else {
        "/home"
    }
    NavHost(
        navController = navController,
        startDestination = currentLocation,
    ) {
        composable("/landing") {
            MainScaffold(navController = navController, showTopBar = false, showBottomBar = false) {
                Landing().Create(it)
            }
        }
        composable("/home", deepLinks = listOf(navDeepLink { uriPattern = "$uri/home" })) {
            MainScaffold(navController = navController, showTopBar = false, showBottomBar = true) {
                Home().Create(it)
            }
        }
        composable(
            "/sign_in",
            deepLinks = listOf(navDeepLink { uriPattern = "$uri/sign_in" })
        ) {
            MainScaffold(navController = navController, showTopBar = false, showBottomBar = false) {
                SignIn().Create(it)
            }
        }
        composable("/sign_up") {
            MainScaffold(navController = navController, showTopBar = false, showBottomBar = false) {
                SignUp().Create(it)
            }
        }
        composable("/forgot_otp") {
            MainScaffold(navController = navController, showTopBar = true, showBottomBar = true) {
                OtpScreen().Create(it)
            }
        }
        composable("/unlock_screen") {
            MainScaffold(navController = navController, showTopBar = true, showBottomBar = true) {
                UnlockScreen().Create(it)
            }
        }
        composable("/forgot_password") {
            MainScaffold(navController = navController, showTopBar = true, showBottomBar = false) {
                ForgotPassword().Create(it)
            }
        }
        composable("/allLocations") {
            MainScaffold(navController = navController, showTopBar = true, showBottomBar = true) {
                AllLocations().Create(it)
            }
        }
    }
}

@Composable
fun MainScaffold(
    navController: NavController = rememberNavController(),
    showTopBar: Boolean,
    showBottomBar: Boolean,
    content: @Composable (Modifier) -> Unit
) {
    Scaffold(
        topBar = {
            if (showTopBar) {
                TextButton(
                    onClick = { navController.popBackStack() }, modifier = Modifier
                        .background(Color.Transparent)
                ) {
                    Text("‹", fontSize = 50.sp, style = MaterialTheme.typography.titleLarge)
                }
            }
        }, bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        selected = navController.currentDestination?.route == "/profile",
                        onClick = { /*navController.navigate("/profile")*/ },
                        icon = {
                            Icon(Icons.Default.Person, "Profile")
                        })
                    NavigationBarItem(
                        selected = navController.currentDestination?.route == "/unlock_screen",
                        onClick = { navController.navigate("/unlock_screen") },
                        icon = {
                            Icon(Icons.Default.Search, "Scan")
                        })
                    NavigationBarItem(
                        selected = navController.currentDestination?.route == "/notifications",
                        onClick = { /*navController.navigate("/notifications")*/ },
                        icon = {
                            Icon(Icons.Default.Notifications, "Notifications")
                        })
                }
            }
        }) { innerPadding ->
        content(Modifier.padding(innerPadding))
    }
}
