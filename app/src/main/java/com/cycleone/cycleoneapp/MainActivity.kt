package com.cycleone.cycleoneapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.cycleone.cycleoneapp.services.CachedNetworkClient
import com.cycleone.cycleoneapp.services.LocationProvider
import com.cycleone.cycleoneapp.services.NavProvider
import com.cycleone.cycleoneapp.ui.components.NormalBackground
import com.cycleone.cycleoneapp.ui.screens.AllLocations
import com.cycleone.cycleoneapp.ui.screens.ForgotPassword
import com.cycleone.cycleoneapp.ui.screens.Home
import com.cycleone.cycleoneapp.ui.screens.Onboarding
import com.cycleone.cycleoneapp.ui.screens.OtpScreen
import com.cycleone.cycleoneapp.ui.screens.Profile
import com.cycleone.cycleoneapp.ui.screens.SignIn
import com.cycleone.cycleoneapp.ui.screens.SignUp
import com.cycleone.cycleoneapp.ui.screens.UnlockScreen
import com.cycleone.cycleoneapp.ui.theme.CycleoneAppTheme
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth


val uri = "cycleone://cycleone.base"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocationProvider.fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        CachedNetworkClient.initialize(application)
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
        "/onboarding"
    } else {
        "/home"
    }
    var showTopBar by remember {
        mutableStateOf(false)
    }

    var showBottomBar by remember {
        mutableStateOf(false)
    }

    var useImageBackground by remember {
        mutableStateOf(false)
    }

    var backgroundImage: Int? by remember {
        mutableStateOf(null)
    }

    MainScaffold(
        navController = navController,
        showTopBar = showTopBar,
        backgroundImage = backgroundImage
    ) { modifier ->
        NavHost(
            navController = navController,
            startDestination = currentLocation,
        ) {
            composable("/onboarding") {
                showTopBar = false
                showBottomBar = false
                backgroundImage = R.drawable.onboard_1
                Onboarding().Create(modifier)
            }
            composable("/home", deepLinks = listOf(navDeepLink { uriPattern = "$uri/home" })) {
                showTopBar = false
                showBottomBar = true
                backgroundImage = R.drawable.dashboard
                Home().Create(modifier)
            }
            composable("/profile") {
                showTopBar = true
                showBottomBar = true
                backgroundImage = R.drawable.normal_background
                Profile().Create(modifier)
            }
            composable(
                "/sign_in",
                deepLinks = listOf(navDeepLink { uriPattern = "$uri/sign_in" })
            ) {
                showTopBar = false
                showBottomBar = false
                backgroundImage = R.drawable.normal_background
                SignIn().Create(modifier)
            }
            composable("/sign_up") {
                showTopBar = false
                showBottomBar = false
                backgroundImage = R.drawable.normal_background
                SignUp().Create(modifier)
            }
            composable("/forgot_otp") {
                showTopBar = true
                showBottomBar = true
                backgroundImage = R.drawable.normal_background
                OtpScreen().Create(modifier)
            }
            composable("/unlock_screen") {
                showTopBar = true
                showBottomBar = true
                backgroundImage = R.drawable.normal_background
                UnlockScreen().Create(modifier)
            }
            composable("/forgot_password") {
                showTopBar = true
                showBottomBar = false
                backgroundImage = R.drawable.normal_background
                ForgotPassword().Create(modifier)
            }
            composable("/allLocations") {
                showTopBar = true
                showBottomBar = true
                backgroundImage = R.drawable.normal_background
                AllLocations().Create(modifier)
            }
        }
    }
}

@Composable
fun MainScaffold(
    navController: NavController = rememberNavController(),
    showTopBar: Boolean,
    backgroundImage: Int? = null,
    content: @Composable (Modifier) -> Unit
) {
    Scaffold(
        snackbarHost = { NavProvider.debugModal() },
        floatingActionButton = { NavProvider.debugButton() },
        topBar = {
            if (showTopBar) {
                Button(
                    onClick = { navController.popBackStack() },
                    colors = ButtonColors(
                        Color.Transparent,
                        Color.White,
                        Color.Transparent,
                        Color.Gray
                    )
                ) {
                    Image(painterResource(R.drawable.left_arrow), "Back")
                }
            }
        }) { innerPadding ->
        if (backgroundImage != null) {
            NormalBackground(Modifier.padding(innerPadding), backgroundImage = backgroundImage) {
                content(Modifier)
            }
        } else {
            content(Modifier.padding(innerPadding))
        }
    }
}

@Composable
@Preview
fun PreviewSignUp() {
    MainScaffold(rememberNavController(), showTopBar = true) {
        SignUp().UI()
    }
}

@Composable
@Preview
fun PreviewSignIn() {
    MainScaffold(rememberNavController(), showTopBar = true) {
        SignIn().UI()
    }
}

@Composable
@Preview
fun PreviewOnboarding() {
    MainScaffold(rememberNavController(), showTopBar = true) {
        Onboarding().UI()
    }
}

@Composable
@Preview
fun PreviewForgotPassword() {
    MainScaffold(rememberNavController(), showTopBar = true) {
        ForgotPassword().UI()
    }
}
