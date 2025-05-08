package com.cycleone.cycleoneapp.services

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    // val permissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val multiplePermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    var locationText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        multiplePermissionsState.launchMultiplePermissionRequest()
    }

    LaunchedEffect(multiplePermissionsState.allPermissionsGranted) {
        if (multiplePermissionsState.allPermissionsGranted) {
            coroutineScope.launch {
                getCurrentLocation(context) { location ->
                    if (location != null) {
                        val lat = location.latitude
                        val lon = location.longitude
                        val address = getAddressFromLocation(context, lat, lon)

                        locationText = "Address: $address"

                        Toast.makeText(context, "Location: $lat, $lon", Toast.LENGTH_SHORT).show()
                    } else {
                        locationText = "Location not available"
                        Toast.makeText(context, "Location not available", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            locationText = "Permission not granted"
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {

        Text(text = locationText)
    }


}

@SuppressLint("MissingPermission")
fun getCurrentLocation(context: Context, onResult: (Location?) -> Unit) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        if (location != null) {
            onResult(location)
        } else {
            // Fallback: request updates
            val locationRequest = LocationRequest.create().apply {
                interval = 10000
                fastestInterval = 5000
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                numUpdates = 1
            }

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    fusedLocationClient.removeLocationUpdates(this)
                    onResult(result.lastLocation)
                }
            }

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }.addOnFailureListener {
        onResult(null)
    }
}

fun getAddressFromLocation(context: Context, latitude: Double, longitude: Double): String? {
    return try {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latitude, longitude, 1) // get 1 result

        if (!addresses.isNullOrEmpty()) {
            val address = addresses[0]
            address.getAddressLine(0) // Full address as a string
        } else {
            "Address not found"
        }
    } catch (e: Throwable) {
        e.printStackTrace()
        "Error fetching address"
    }
}
