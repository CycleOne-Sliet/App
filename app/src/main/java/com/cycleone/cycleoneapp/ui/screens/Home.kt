package com.cycleone.cycleoneapp.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.cycleone.cycleoneapp.R
import com.cycleone.cycleoneapp.services.LocationProvider
import com.cycleone.cycleoneapp.services.NavProvider
import com.cycleone.cycleoneapp.ui.components.MapDisplay
import com.cycleone.cycleoneapp.ui.theme.monsterratFamily
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.Priority
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import ovh.plrapps.mapcompose.api.addMarker
import ovh.plrapps.mapcompose.api.scrollTo

class Home {

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun Create(
        modifier: Modifier = Modifier,
        authInstance: FirebaseAuth? = FirebaseAuth.getInstance(),
        navController: NavController
    ) {
        var username by remember { mutableStateOf("") }

        if (ActivityCompat.checkSelfPermission(
                LocalContext.current,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                LocalContext.current,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Handle permissions
        }

        if (authInstance != null) {
            val user = authInstance.currentUser
            username = user?.displayName ?: ""
            if (user == null) {
                navController.navigate("/sign_in")
            }
        }

        val permissionStates = rememberMultiplePermissionsState(
            listOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            )
        )

        UI(modifier, username, permissionStates, navController)
    }

    @SuppressLint("MissingPermission")
    @OptIn(ExperimentalPermissionsApi::class)
    @Preview
    @Composable
    fun UI(
        modifier: Modifier = Modifier,
        username: String = "Sandeep Kumar",
        permissionState: MultiplePermissionsState = rememberMultiplePermissionsState(listOf()),
        navController: NavController = rememberNavController()
    ) {
        val mapDisplay by remember { mutableStateOf(MapDisplay()) }
        val coroutineScope = rememberCoroutineScope()

        val recentStandName = remember { mutableStateOf("Campus Main Gate") }
        val recentStandNumber = remember { mutableStateOf("Stand #12") }

        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            NavProvider.drawer?.open()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Image(
                        painter = painterResource(R.drawable.menu),
                        contentDescription = "Menu",
                        modifier = Modifier.size(32.dp)
                    )
                }
                Image(
                    painter = painterResource(R.drawable.logo_cycleone_new_01),
                    contentDescription = "Logo",
                    modifier = Modifier.size(width = 58.dp, height = 37.dp)
                )
                Button(
                    onClick = { navController.navigate("/history") }
                ) {
                    Image(
                        painter = painterResource(R.drawable.vector_1_),
                        contentDescription = "History",
                        modifier = Modifier.size(32.dp)
                    )
                }
                Button(
                    onClick = { navController.navigate("/coinpage") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Icon(Icons.Default.AccountBalance, contentDescription = "coin")
                }
            }

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    "Hello !!!",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = monsterratFamily,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 5.dp)
                )
                Text(
                    username,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Light,
                    fontFamily = monsterratFamily,
                    color = Color.White
                )

                // Recent Accessed Stand Section
                Text(
                    "Recent Cycle Accessed",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = monsterratFamily,
                    color = Color.White,
                    modifier = Modifier.padding(top = 15.dp, bottom = 8.dp)
                )
                Button(
                    onClick = {
                        // Add your future navigation logic here
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.White, RoundedCornerShape(10.dp))
                        .background(Color(0xff252322)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = recentStandName.value,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = monsterratFamily,
                            color = Color.White
                        )
                        Text(
                            text = recentStandNumber.value,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Light,
                            fontFamily = monsterratFamily,
                            color = Color.Gray
                        )
                    }
                }

                // Location Map Section
                Column(
                    modifier = Modifier
                        .border(2.dp, Color.White, RoundedCornerShape(10.dp))
                        .background(Color(0xff252322))
                        .padding(15.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 250.dp, max = 300.dp)
                            .border(2.dp, Color.White, RoundedCornerShape(5.dp))
                    ) {
                        if (permissionState.allPermissionsGranted) {
                            LocationProvider.fusedLocationClient.getCurrentLocation(
                                CurrentLocationRequest.Builder()
                                    .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                                    .setGranularity(Granularity.GRANULARITY_FINE)
                                    .build(), null
                            ).addOnSuccessListener {
                                it?.let { location ->
                                    val coords = mapDisplay.locationToNormalizedCoords(location)
                                    coroutineScope.launch {
                                        mapDisplay.state.scrollTo(coords.first, coords.second, 0.15f)
                                        mapDisplay.state.addMarker(
                                            "Current Location",
                                            coords.first,
                                            coords.second
                                        ) {
                                            Icon(Icons.Default.Person, contentDescription = "Location")
                                        }
                                    }
                                }
                            }
                            mapDisplay.Create(modifier = Modifier.fillMaxSize())
                        } else {
                            Column {
                                val message = if (permissionState.shouldShowRationale) {
                                    "The location permission is required to show current location"
                                } else {
                                    "Without these permissions, the app cannot function properly.\nPlease grant the permissions."
                                }
                                Text(message, color = Color.White)
                                Button(onClick = { permissionState.launchMultiplePermissionRequest() }) {
                                    Text("Request location permissions")
                                }
                            }
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(90.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xff252322))
                        .border(3.dp, Color(0xffff6b35), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { navController.navigate("/notification") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                    ) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notification")
                    }
                    Button(
                        onClick = { navController.navigate("/feedbackPage") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = "Phone")
                    }
                }

                Button(
                    onClick = { navController.navigate("/unlock_screen") },
                    modifier = Modifier
                        .padding(bottom = 15.dp)
                        .fillMaxHeight()
                        .border(3.dp, Color(0xffff6b35), RoundedCornerShape(10.dp)),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xff252322))
                ) {
                    Image(
                        painter = painterResource(R.drawable.qr_code_1_),
                        contentDescription = "Scan",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .padding(vertical = 6.dp)
                            .size(56.dp)
                    )
                }
            }
        }
    }
}
