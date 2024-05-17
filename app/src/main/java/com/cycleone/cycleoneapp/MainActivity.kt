package com.cycleone.cycleoneapp

import android.os.Bundle
import android.util.Log
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cycleone.cycleoneapp.services.CloudFunctions
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CloudFunctions.Connect()
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
    val noBottomBar = listOf("/landing", "/sign_in", "/sign_up")
    val noTopBar = listOf("/landing", "/home")
    val shouldNotShowBottomBar =
        navController.currentBackStackEntryAsState().value?.destination?.route in noBottomBar
    val shouldNotShowTopBar =
        navController.currentBackStackEntryAsState().value?.destination?.route in noTopBar
    Log.d("ShouldNotBottom", shouldNotShowBottomBar.toString())
    Log.d(
        "ShouldNotBottom",
        navController.currentBackStackEntryAsState().value?.destination?.route.toString()
    )
    Scaffold(
        topBar = {
            if (!shouldNotShowTopBar) {

                TextButton(
                    onClick = { navController.popBackStack() }, modifier = Modifier
                        .background(Color.Transparent)
                ) {
                    Text("‹", fontSize = 50.sp, style = MaterialTheme.typography.titleLarge)
                }
            }
        }, bottomBar = {
            if (!shouldNotShowBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        selected = false,
                        onClick = { navController.navigate("/profile") },
                        icon = {
                            Icon(Icons.Default.Person, "Profile")
                        })
                    NavigationBarItem(
                        selected = false,
                        onClick = { navController.navigate("/unlock_screen") },
                        icon = {
                            Icon(Icons.Default.Search, "Scan")
                        })
                    NavigationBarItem(
                        selected = false,
                        onClick = { navController.navigate("/notifications") },
                        icon = {
                            Icon(Icons.Default.Notifications, "Notifications")
                        })
                }
            }
        }) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "/landing",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("/landing") {
                Landing().Create()
            }
            composable("/home") {
                Home().Create()
            }
            composable("/sign_in") {
                SignIn().Create()
            }
            composable("/sign_up") {
                SignUp().Create()
            }
            composable("/forgot_otp") {
                OtpScreen().Create()
            }
            composable("/unlock_screen") {
                UnlockScreen().Create()
            }
            composable("/forgot_password") {
                ForgotPassword().Create()
            }
            composable("/allLocations") {
                AllLocations().Create()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CycleoneAppTheme(darkTheme = false) {
        BaseController()
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreviewDark() {
    CycleoneAppTheme(darkTheme = true) {
        BaseController()
    }
}
