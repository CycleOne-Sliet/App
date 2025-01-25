package com.cycleone.cycleoneapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
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
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
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
import com.google.firebase.auth.FirebaseAuth


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

    var useNormalBackground by remember {
        mutableStateOf(false)
    }

    MainScaffold(
        navController = navController,
        showTopBar = showTopBar,
        useNormalBackground = useNormalBackground,
        showBottomBar = showBottomBar
    ) { modifier ->
        NavHost(
            navController = navController,
            startDestination = currentLocation,
        ) {
            composable("/onboarding") {
                showTopBar = false
                showBottomBar = false
                useNormalBackground = false
                Onboarding().Create(modifier)
            }
            composable("/home", deepLinks = listOf(navDeepLink { uriPattern = "$uri/home" })) {
                showTopBar = false
                showBottomBar = true
                useNormalBackground = true
                Home().Create(modifier)
            }
            composable("/profile") {
                showTopBar = true
                showBottomBar = true
                useNormalBackground = true
                Profile().Create(modifier)
            }
            composable(
                "/sign_in",
                deepLinks = listOf(navDeepLink { uriPattern = "$uri/sign_in" })
            ) {
                showTopBar = false
                showBottomBar = false
                useNormalBackground = true
                SignIn().Create(modifier)
            }
            composable("/sign_up") {
                showTopBar = false
                showBottomBar = false
                useNormalBackground = true
                SignUp().Create(modifier)
            }
            composable("/forgot_otp") {
                showTopBar = true
                showBottomBar = true
                useNormalBackground = true
                OtpScreen().Create(modifier)
            }
            composable("/unlock_screen") {
                showTopBar = true
                showBottomBar = true
                useNormalBackground = true
                UnlockScreen().Create(modifier)
            }
            composable("/forgot_password") {
                showTopBar = true
                showBottomBar = false
                useNormalBackground = true
                ForgotPassword().Create(modifier)
            }
            composable("/allLocations") {
                showTopBar = true
                showBottomBar = true
                useNormalBackground = true
                AllLocations().Create(modifier)
            }
        }
    }
}

@Composable
fun MainScaffold(
    navController: NavController = rememberNavController(),
    showTopBar: Boolean,
    showBottomBar: Boolean,
    useNormalBackground: Boolean,
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
        }, bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        selected = navController.currentDestination?.route == "/profile",
                        onClick = {
                            navController.navigate("/profile")

                            {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(Icons.Default.Person, "Profile")
                        })
                    NavigationBarItem(
                        selected = navController.currentDestination?.route == "/unlock_screen",
                        onClick = {
                            navController.navigate("/unlock_screen") {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        },
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
        if (useNormalBackground) {
            NormalBackground(Modifier.padding(innerPadding)) {
                content(Modifier)
            }
        } else {
            content(Modifier.padding(innerPadding))
        }
    }
}
