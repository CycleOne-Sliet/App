package com.cycleone.cycleoneapp.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
        var username by remember {
            mutableStateOf("")
        }

        if (ActivityCompat.checkSelfPermission(
                LocalContext.current,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                LocalContext.current,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

        }
        if (authInstance != null) {
            val user = authInstance.currentUser
            username = user?.displayName ?: ""
            Log.d("User", user.toString())
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
        UI(modifier, username, permissionStates, navController = navController)
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
        val mapDisplay by remember {
            mutableStateOf(MapDisplay())
        }

        val coroutineScope = rememberCoroutineScope()

        Column(
            modifier = modifier
                .fillMaxSize(),
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
                    enabled = true,
                    onClick = {
                        Log.d("coroutine", "Launched NavProvider Coroutine")
                        coroutineScope.launch {
                            Log.d("drawer", "Launched drawer open Coroutine")
                            if (NavProvider.drawer == null) {
                                Log.d("drawer", "Drawer is null")
                            } else {
                                Log.d("drawer", "Drawer is not null")
                            }
                            NavProvider.drawer!!.open()
                            Log.d("coroutine", "Launched completed drawer open Coroutine")
                        }
                        Log.d("coroutine", "Launched completed NavProvider Coroutine")
                    }) {
                    Image(
                        painter = painterResource(R.drawable.menu),
                        "Menu",
                        modifier = Modifier
                            .width(32.dp)
                            .height(32.dp)
                    )
                }
                Image(
                    painter = painterResource(R.drawable.logo_cycleone_new_1),
                    "Logo",
                    modifier = Modifier
                        .width(58.dp)
                        .height(37.dp)
                )
                Button(enabled = false, onClick = { /*TODO*/ }) {
                    Image(
                        painter = painterResource(R.drawable.vector_1_),
                        "Menu",
                        modifier = Modifier
                            .width(32.dp)
                            .height(32.dp)
                    )
                }
            }
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Hello !!!",
                        modifier = Modifier.padding(bottom = 5.dp),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = monsterratFamily,
                        color = Color.White
                    )
                    Text(
                        username,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Light,
                        fontFamily = monsterratFamily,
                        color = Color.White
                    )
                    Text(
                        "Your last session",
                        modifier = Modifier.padding(vertical = 15.dp),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = monsterratFamily,
                        color = Color.White
                    )
                }
                Column(
                    modifier = Modifier
                        .border(2.dp, Color.White, RoundedCornerShape(10.dp))
                        .background(Color(0xff252322)),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .padding(15.dp)
                            .fillMaxWidth()
                            .heightIn(min = 250.dp, max = 300.dp)
                            .border(2.dp, Color.White, RoundedCornerShape(5.dp)),
                    ) {
                        if (permissionState.allPermissionsGranted) {
                            LocationProvider.fusedLocationClient.getCurrentLocation(
                                CurrentLocationRequest.Builder()
                                    .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                                    .setGranularity(Granularity.GRANULARITY_FINE).build(), null
                            ).addOnSuccessListener {
                                if (it != null) {
                                    Log.d("location", it.toString())
                                    val location = mapDisplay.locationToNormalizedCoords(it)
                                    Log.d("normLocation", location.toString())
                                    coroutineScope.launch {
                                        mapDisplay.state.scrollTo(
                                            0.0,
                                            0.0,
                                        )

                                        mapDisplay.state.scrollTo(
                                            location.first,
                                            location.second,
                                            0.15f
                                        )
                                        mapDisplay.state.addMarker(
                                            "Current Location",
                                            location.first,
                                            location.second
                                        ) {
                                            Icon(Icons.Default.Person, "Current Location")
                                        }
                                    }
                                }
                            }
                            mapDisplay.Create(
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {

                            Column {
                                val textToShow = if (permissionState.shouldShowRationale) {
                                    "The location permission is required to show current location"
                                } else {
                                    "Without these permission, the app cannot function" +
                                            "Please grant the permissions\n" + "The location will be logged"
                                }
                                Text(textToShow)
                                Button(
                                    onClick = { permissionState.launchMultiplePermissionRequest() }) {
                                    Text("Request location permissions")
                                }
                            }
                        }

                    }



                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .padding(15.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                "Distance",
                                fontFamily = monsterratFamily,
                                fontSize = 20.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Light
                            )
                            Text(
                                "2.67Km",
                                color = Color(0xffff6b35),
                                fontFamily = monsterratFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 24.sp
                            )
                        }
                        Column {
                            Text(
                                "Time",
                                fontFamily = monsterratFamily,
                                fontSize = 20.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Light
                            )
                            Text(
                                "27 min",
                                color = Color(0xffff6b35),
                                fontFamily = monsterratFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 24.sp
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8F)
                    .height(90.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xff252322))
                        .border(3.dp, Color(0xffff6b35), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp),
                    Arrangement.SpaceBetween,
                    Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors()
                            .copy(containerColor = Color.Transparent)
                    ) {
                        Icon(Icons.Default.Notifications, "Notification")
                    }
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors()
                            .copy(containerColor = Color.Transparent)
                    ) {
                        Icon(Icons.Default.Phone, "Phone")
                    }
                }

                Button(
                    onClick = { navController.navigate("/unlock_screen") },
                    modifier = Modifier
                        .padding(bottom = 15.dp)
                        .fillMaxHeight()
                        .border(3.dp, Color(0xffff6b35), RoundedCornerShape(10.dp)),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors()
                        .copy(containerColor = Color(0xff252322))
                ) {
                    Image(
                        painter = painterResource(R.drawable.qr_code_1_),
                        "Scan",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .padding(vertical = 6.dp)
                            .width(56.dp)
                            .height(56.dp)
                    )
                }

            }
        }
    }
}