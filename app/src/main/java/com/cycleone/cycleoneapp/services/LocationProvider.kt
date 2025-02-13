package com.cycleone.cycleoneapp.services


import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LocationProvider : Service() {

    companion object {
        lateinit var fusedLocationClient: FusedLocationProviderClient
    }

    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var serviceLooper: Looper? = null
    private var serviceHandler: Handler? = null

    private var currentSession: DocumentReference? = null

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createLocationRequest()

        // Start a background thread to handle location updates
        HandlerThread("LocationUpdatesThread").apply {
            start()
            serviceLooper = looper
            serviceHandler = Handler(looper)
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationResult.lastLocation?.let { location ->
                    // Handle the received location update here on the background thread
                    onNewLocation(location)
                }
            }
        }
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000).apply {
            setWaitForAccurateLocation(false)
            setMinUpdateIntervalMillis(5000)
            setMaxUpdateDelayMillis(15000)
        }.build()
    }


    private fun onNewLocation(location: Location) {
        // Format and display location information

        Firebase.firestore.collection("/path_locations").add(
            mapOf(
                Pair("session", currentSession),
                Pair("lat", location.latitude),
                Pair("lon", location.longitude),
                Pair("time", location.time),
                Pair("speed", location.speed)
            )
        )

        val locationText = "Latitude: ${location.latitude}, Longitude: ${location.longitude}"
        // You can use a Notification to show location updates in the background
        // For example:  showNotification(locationText)
        println("Location Update (Background Thread): $locationText") // Now running on background thread
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        requestLocationUpdates()
        // Keeps the service running even after app is closed (if needed)
        return START_STICKY
    }

    private fun requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Use the serviceLooper (background looper) here
            Firebase.auth.currentUser?.let { user ->
                Firebase.firestore.collection("/sessions").add(
                    mapOf(
                        Pair("user", user.uid),
                        Pair("timestamp", System.currentTimeMillis())
                    )
                ).addOnSuccessListener {
                    currentSession = it
                }
                serviceLooper?.let {
                    fusedLocationClient.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        it // Pass the background looper
                    )
                }
            }
        }
    }

    private fun removeLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        stopSelf() // Stop the service when location updates are no longer needed
    }


    override fun onDestroy() {
        removeLocationUpdates()
        serviceLooper?.quitSafely() // Quit the background looper thread
        serviceLooper = null
        serviceHandler = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null // Services started with startService do not need to bind
    }

}