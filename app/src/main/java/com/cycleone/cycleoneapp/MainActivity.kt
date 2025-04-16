package com.cycleone.cycleoneapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.cycleone.cycleoneapp.services.CachedNetworkClient
import com.cycleone.cycleoneapp.services.LocationProvider
import com.cycleone.cycleoneapp.services.NavProvider
import com.cycleone.cycleoneapp.ui.components.FancyButton
import com.cycleone.cycleoneapp.ui.components.NormalBackground
import com.cycleone.cycleoneapp.ui.screens.AllLocations
import com.cycleone.cycleoneapp.ui.screens.CoinPage
import com.cycleone.cycleoneapp.ui.screens.EditProfile
import com.cycleone.cycleoneapp.ui.screens.FeedbackScreen
import com.cycleone.cycleoneapp.ui.screens.ForgotPassword
import com.cycleone.cycleoneapp.ui.screens.HistoryScreen
import com.cycleone.cycleoneapp.ui.screens.Home
import com.cycleone.cycleoneapp.ui.screens.NotificationScreen
import com.cycleone.cycleoneapp.ui.screens.Onboarding
import com.cycleone.cycleoneapp.ui.screens.OtpScreen
import com.cycleone.cycleoneapp.ui.screens.Profile
import com.cycleone.cycleoneapp.ui.screens.SignIn
import com.cycleone.cycleoneapp.ui.screens.SignUp
import com.cycleone.cycleoneapp.ui.screens.UnlockScreen
import com.cycleone.cycleoneapp.ui.theme.CycleoneAppTheme
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


const val uri = "cycleone://cycleone.base"

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

data class DrawerPath(val name: String, val dest: String)

@Composable
fun BaseController(navController: NavHostController = rememberNavController()) {
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
                Onboarding().Create(modifier, navController)
            }
            composable("/home", deepLinks = listOf(navDeepLink { uriPattern = "$uri/home" })) {
                showTopBar = false
                showBottomBar = true
                backgroundImage = R.drawable.dashboard
                Home().Create(modifier, navController = navController)
            }
            composable("/profile") {
                showTopBar = true
                showBottomBar = true
                backgroundImage = R.drawable.normal_background
                Profile().Create(modifier, navController)
            }
            composable("/edit_profile") {
                showTopBar = true
                showBottomBar = true
                backgroundImage = R.drawable.normal_background
                EditProfile().Create(modifier, navController)
            }
            composable(
                "/sign_in",
                deepLinks = listOf(navDeepLink { uriPattern = "$uri/sign_in" })
            ) {
                showTopBar = false
                showBottomBar = false
                backgroundImage = R.drawable.normal_background
                SignIn().Create(modifier, navController)
            }
            composable("/sign_up") {
                showTopBar = false
                showBottomBar = false
                backgroundImage = R.drawable.normal_background
                SignUp().Create(modifier, navController)
            }
            composable("/forgot_otp") {
                showTopBar = true
                showBottomBar = true
                backgroundImage = R.drawable.normal_background
                OtpScreen().Create(modifier, navController)
            }
            composable("/unlock_screen") {
                showTopBar = true
                showBottomBar = true
                backgroundImage = R.drawable.normal_background
                UnlockScreen().Create(modifier, navController)
            }
            composable("/forgot_password") {
                showTopBar = true
                showBottomBar = false
                backgroundImage = R.drawable.normal_background
                ForgotPassword().Create(modifier, navController)
            }
            composable("/allLocations") {
                showTopBar = true
                showBottomBar = true
                backgroundImage = R.drawable.normal_background
                AllLocations().Create(modifier, navController)
            }
            composable("/feedbackPage") {
                showTopBar = true
                showBottomBar = true
                backgroundImage = R.drawable.normal_background
                FeedbackScreen(modifier, navController)
            }
            composable("/notification") {
                showTopBar = true
                showBottomBar = true
                backgroundImage = R.drawable.normal_background
                NotificationScreen(modifier, navController)
            }
            composable("/history") {
                showTopBar = true
                showBottomBar = true
                backgroundImage = R.drawable.normal_background
                HistoryScreen(modifier, navController)
            }
            composable("/coinpage") {
                showTopBar = true
                showBottomBar = true
                backgroundImage = R.drawable.normal_background
                CoinPage(modifier, navController)
            }

        }
    }
}

val drawerPaths =
    listOf(DrawerPath("My Profile", "/profile"))


@Composable
fun MainScaffold(
    navController: NavController = rememberNavController(),
    showTopBar: Boolean,
    backgroundImage: Int? = null,
    content: @Composable (Modifier) -> Unit
) {
    val user = FirebaseAuth.getInstance().currentUser
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    NavProvider.drawer = drawerState
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.fillMaxHeight()) {
                Column(
                    modifier = Modifier.fillMaxHeight().width(320.dp).padding(start = 8.dp),
                 //  verticalArrangement = Arrangement.SpaceBetween
                ) {
                        Box(
                            modifier = Modifier
                                .requiredWidth(320.dp)
                                .requiredHeight(200.dp).background(Color(0xffff6b35), RoundedCornerShape(topEnd = 16.dp)),
                            contentAlignment = Alignment.Center)
                         {
                         /*   AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(user?.photoUrl)
                                    .build(),
                                contentDescription = "Profile Photo",
                                fallback = rememberVectorPainter(Icons.Default.Person),
                                placeholder = rememberVectorPainter(Icons.Default.Person),
                                modifier = Modifier
                                    .fillMaxWidth(),
                                alignment = Alignment.Center,
                                contentScale = ContentScale.FillWidth
                            )*/
                            Column(modifier = Modifier.fillMaxSize()) {
                                Button(
                                    onClick = { coroutineScope.launch { drawerState.close() } },
                                    colors = ButtonColors(
                                        Color.Transparent,
                                        Color.Gray,
                                        Color.Transparent,
                                        Color.Black
                                    )
                                ) {
                                    Image(painterResource(R.drawable.left_arrow), "Back",
                                       colorFilter = ColorFilter.tint(color = Color.Black) )
                                }
                                Text(text = "CycleOne", fontSize = 64.sp,
                                    color = Color.Black)
                            }


                        }
                      //  user?.displayName?.let { Text(it, modifier = Modifier.padding(16.dp)) }

                    HorizontalDivider(thickness = 3.dp)
                    Spacer(modifier = Modifier.height(15.dp))

                    Column(
                        modifier = Modifier.clip(RoundedCornerShape(25.dp, 0.dp, 0.dp, 0.dp))
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceBetween

                    ) {
                        drawerPaths.forEach { drawerPath ->
                            NavigationDrawerItem(
                                label = { Text(text = "My Profile",
                                    fontSize = 18.sp,
                                    color = Color(0xffff6b35),
                                    modifier = Modifier.weight(1f)) },
                                icon = {
                                    Icon(imageVector = Icons.Default.Person, contentDescription = null,
                                        tint = Color(0xffff6b35))
                                },
                                selected = false,
                                onClick = {
                                    navController.navigate(drawerPath.dest)
                                    coroutineScope.launch {
                                        drawerState.close()
                                    }
                                }
                            )

                        }
                        FancyButton(modifier = Modifier.fillMaxWidth(), text = "Logout", onClick = {
                            FirebaseAuth.getInstance().signOut()
                            coroutineScope.launch {
                                drawerState.close()
                            }
                            navController.navigate("/sign_in")
                        })
                    }

                }
            }
        }
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
                NormalBackground(
                    Modifier.padding(innerPadding),
                    backgroundImage = backgroundImage
                ) {
                    content(Modifier)
                }
            } else {
                content(Modifier.padding(innerPadding))
            }
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
