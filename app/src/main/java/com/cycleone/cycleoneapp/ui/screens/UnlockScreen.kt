package com.cycleone.cycleoneapp.ui.screens

import android.Manifest
import android.content.Context
import android.net.MacAddress
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.coroutineScope
import androidx.navigation.NavController
import com.cycleone.cycleoneapp.R
import com.cycleone.cycleoneapp.services.CloudFunctions
import com.cycleone.cycleoneapp.services.NavProvider
import com.cycleone.cycleoneapp.services.NavProvider.Companion.showDebugModal
import com.cycleone.cycleoneapp.services.QrCode
import com.cycleone.cycleoneapp.services.Stand
import com.cycleone.cycleoneapp.ui.components.FancyButton
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.security.InvalidParameterException

fun decodeHexFromStr(hex: String): ByteArray {
    if (hex.length % 2 != 0) {
        throw InvalidParameterException("Fetching wrong length of hex: ${hex.length}")
    }
    return ByteArray(hex.length / 2) {
        Integer.parseInt(hex, it * 2, (it + 1) * 2, 16).toByte()
    }
}

@OptIn(ExperimentalPermissionsApi::class)
class UnlockScreen {

    companion object {
        @Volatile
        var transactionRunning = 0
    }

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun Create(
        modifier: Modifier = Modifier,
        navController: NavController
    ) {

        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid
        if (uid == null) {
            navController.navigate("/sign_in")
            return
        }
        var userHasCycle: Boolean? by remember {
            mutableStateOf(
                null
            )
        }
        var userCycleId: Long? by remember {
            mutableStateOf(null)
        }


        val lifecycleOwner = LocalLifecycleOwner.current
        val lifecycleCoroutineScope = lifecycleOwner.lifecycle.coroutineScope

        Firebase.firestore.collection("users").document(uid).get()
            .addOnSuccessListener {
                userHasCycle = it.data?.get("HasCycle") as Boolean?
                userCycleId = it.data?.get("CycleOccupied") as Long?
            }
        Log.d("HasCycleCompos", userHasCycle.toString())
        Log.d("UID", uid.toString())
        var showCamera by remember {
            mutableStateOf(false)
        }
        val context = LocalContext.current
        val permissionStates = rememberMultiplePermissionsState(
            listOf(
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.CHANGE_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
        )




        UI(
            modifier,
            user = user,
            onVerificationRequest = {
                navController.navigate("/profile")
            },
            onScanSuccess = { qr ->

                showCamera = false
                Log.d("code", "camera false")
                if (!user.isEmailVerified) {

                  //  Toast.makeText(context, "Email is not verified", Toast.LENGTH_SHORT).show()
                    return@UI
                }

                lifecycleCoroutineScope.launch {
                    withContext(Dispatchers.IO) {
                        try {
                            val userSnap = Firebase.firestore.collection("users").document(uid).get().await()
                            val userSnapData = userSnap.data
                            val currentCoins = (userSnapData?.get("Coins") as? Long ?: 0L).toInt()

                            Log.d("currentCoins", currentCoins.toString())

                            if (currentCoins < 5) {
                              //  Toast.makeText(context, "You need at least 5 coins to unlock a cycle", Toast.LENGTH_LONG).show()
                                NavProvider.addLogEntry("You need at least 5 coins to unlock a cycle")
                                return@withContext
                            }



                            userHasCycle = Firebase.firestore.collection("users")
                                .document(uid).get().await().data?.get("HasCycle") as? Boolean
                         try{
                            triggerStandSeq(qr, context, userHasCycle)
                         }catch (e: Exception) {
                            // Toast.makeText(context, "Failed to communicate with stand: ${e.message}", Toast.LENGTH_LONG).show()
                             return@withContext
                         }
                            NavProvider.addLogEntry("Unlocking cycle Successfully")
                            Log.d("userHasCycle", userHasCycle.toString())

                            // Deduct 5 coins
                            if (userHasCycle == true){

                            val updatedCoins = currentCoins - 5
                            userSnap.reference.update("Coins", updatedCoins).await()

                            }

                            val updatedSnap = Firebase.firestore.collection("users").document(uid).get().await()
                            if (updatedSnap.data?.get("HasCycle") != null) {
                                userHasCycle = updatedSnap.data?.get("HasCycle") as Boolean
                            }

                        } catch (e: Throwable) {
                            Log.e("onScanSuccess", "Error: ${e.message}", e)
                            Toast.makeText(context, "Something went wrong: ${e.message}", Toast.LENGTH_LONG).show()
                        }

                    }
                }

            },
            showCamera = showCamera,
            permissionState = permissionStates,
            buttonClick = {
                if (!CloudFunctions.isConnected()) {
                    throw Error("Network Not Available")
                }
                showCamera = true
            },
            userHasCycle = userHasCycle,
            userCycleId = userCycleId,
            loadUserData = {
                val userData = Firebase.firestore.collection("users").document(uid).get().await()
                userHasCycle = userData.data?.get("HasCycle") as Boolean?
                userCycleId = userData.data?.get("CycleOccupied") as Long?
            }
        )
    }

    @Preview
    @Composable
    fun UI(
        modifier: Modifier = Modifier,
        onScanSuccess: suspend (String) -> Unit = {},
        onVerificationRequest: () -> Unit = {},
        permissionState: MultiplePermissionsState = rememberMultiplePermissionsState(listOf()),
        showCamera: Boolean = false,
        buttonClick: suspend () -> Unit = {},
        userHasCycle: Boolean? = null,
        userCycleId: Long? = null,
        user: FirebaseUser? = null,
        loadUserData: suspend () -> Unit = {}
    ) {
        var loadedUserData by remember {
            mutableStateOf(false)
        }



        LaunchedEffect(loadUserData) {
            if (!loadedUserData) {
                loadedUserData = true
                loadUserData()
            }
        }
        val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
        Column(
            modifier = modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            if (showCamera) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    QrCode.startCamera(
                        lifecycleOwner = lifecycleOwner,
                        onSuccess = onScanSuccess
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(painter = painterResource(id = R.drawable.unlock_image), "Unlock Image")
                    Text("Hi ${user?.displayName}\n\n")
                    if (loadedUserData) {

                        if (userHasCycle == true) {
                            Text("You currently have a cycle allocated\n\n")
                            Text("CycleId: $userCycleId")
                        }

                        if (userHasCycle != true) {
                            Text("You currently don't have a cycle allocated\n\n")
                        }

                        if (permissionState.allPermissionsGranted) {
                            if (transactionRunning > 0) {
                                CircularProgressIndicator(
                                    modifier = Modifier.width(64.dp),
                                )
                            } else {
                                if (user?.isEmailVerified == true) {
                                    Text(
                                        "Scan QR Code to \nUnlock",
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(bottom = 20.dp)
                                    )
                                } else {
                                    Text(
                                        "Your email is not verified, resend verification link by going to the profile section",
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(bottom = 20.dp)
                                    )
                                }
                                if (user?.isEmailVerified == true) {
                                    FancyButton(
                                        modifier = Modifier.fillMaxWidth(),
                                        onClick = {
                                            buttonClick()
                                        }) { loading ->
                                        if (loading) {
                                            CircularProgressIndicator()
                                        } else {
                                            if (userHasCycle == true) {
                                                Text("Scan to return cycle")
                                            } else {
                                                Text("Scan to unlock cycle")
                                            }
                                            Icon(Icons.Default.Search, "QR")
                                        }
                                    }
                                } else {
                                    FancyButton(modifier = Modifier.fillMaxWidth(), onClick  = {onVerificationRequest()})
                                }
                            }
                        } else {
                            Column {
                                val textToShow = if (permissionState.shouldShowRationale) {
                                    "The camera permission is required to scan the stand's QR, and the wifi permissions are required to connect to the stand"
                                } else {
                                    "Without these permission, the app cannot function" +
                                            "Please grant the permissions\n" + "These permissions will only be used while scanning the QR"
                                }
                                Text(textToShow)
                                Button(
                                    onClick = { permissionState.launchMultiplePermissionRequest() }) {
                                    Text("Request camera and wifi permissions")
                                }
                            }
                        }
                    } else {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }

    private suspend fun triggerStandSeq(
        qrCode: String,
        context: Context,
        userHasCycle: Boolean?,

    ) {
        val macHex = decodeHexFromStr(qrCode.trim())
        Log.d("qrcode","qr is $macHex")
        if (macHex.size != 6) {
            Log.e("DecodingQr", "Fked Up: Invalid Qr Code")
            throw Error("Invalid QR Code")
        }

        val mac: MacAddress = MacAddress.fromBytes(macHex)
        NavProvider.addLogEntry("Mac Address: $mac")
        Log.d("macad2","stand Connected $mac")
        try {
            val socket = Stand.connect(
                mac, context

            )
            Log.d("macad","stand Connected")
            if (socket == null) {
                throw IllegalStateException("Couldn't connect to WiFi")
            }
          //  val standIsUnlocked = Stand.isUnlocked(socket)
            val standStatusToken = Stand.getToken(socket)
            val cloudToken = CloudFunctions.token(standStatusToken)
            val standToken = Stand.trigger(socket, cloudToken)
            CloudFunctions.putToken(standToken)
            Stand.disconnect()
        } catch (e: Throwable) {
            Stand.disconnect()
            throw Error(e.message)
        }
    }
}
