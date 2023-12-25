@file:OptIn(ExperimentalMaterial3Api::class)

package com.cycleone.ttest2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cycleone.ttest2.ui.Dashboard
import com.cycleone.ttest2.ui.Home
import com.cycleone.ttest2.ui.JourneyScreen
import com.cycleone.ttest2.ui.SignIn
import com.cycleone.ttest2.ui.SignUp
import com.cycleone.ttest2.ui.UnlockScreen
import com.cycleone.ttest2.ui.theme.TTest2Theme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TTest2Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Screen()
                }
            }
        }
    }
}

@Composable
@Preview
fun Screen() {
    val navController = rememberNavController()
    val authController = FirebaseAuth.getInstance()
    NavHost(navController = navController, startDestination = "/home") {
        composable("/home") {
            Home(navController, authController).HomePage()
        }
        composable("/sign_in") {
            SignIn(navController, authController).Login()
        }
        composable("/sign_up") {
            SignUp(navController, authController).CreateAccount()
        }
        composable("/dashboard") {
            Dashboard(navController, authController).StandAreas()
        }
        composable("/unlock") {
            UnlockScreen(navController).StandAreas()
        }
        composable("/journey") {
            JourneyScreen(navController).Screen()
        }
    }
}
