package com.cycleone.cycleoneapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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
    NavHost(navController = navController, startDestination = "/landing") {
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
