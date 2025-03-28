package com.cycleone.cycleoneapp.ui.screens

import android.Manifest
import android.content.Context
import android.net.MacAddress
import android.util.Log
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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cycleone.cycleoneapp.R
import com.cycleone.cycleoneapp.services.CloudFunctions
import com.cycleone.cycleoneapp.services.NavProvider
import com.cycleone.cycleoneapp.services.QrCode
import com.cycleone.cycleoneapp.services.Stand
import com.cycleone.cycleoneapp.ui.components.FancyButton
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import java.security.InvalidParameterException

fun decodeHexFromStr(hex: String): ByteArray {
    if (hex.length % 2 != 0) {
        throw InvalidParameterException("Fking wrong length of hex: ${hex.length}")
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
            onScanSuccess = { qr ->
                showCamera = false
                if (!user.isEmailVerified) {
                    return@UI
                }
                userHasCycle = (Firebase.firestore.collection("users").document(uid).get()
                    .await().data?.get("HasCycle") ?: true) as Boolean?
                if (userHasCycle == null) {
                    return@UI
                }
                triggerStandSeq(
                    qr,
                    context, userHasCycle
                )
                val userSnap = Firebase.firestore.collection("users").document(uid).get().await()
                if (userSnap.data?.get("HasCycle") != null) {
                    userHasCycle = userSnap.data?.get("HasCycle")!! as Boolean
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
                                Text(
                                    "Scan QR Code to \nUnlock",
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(bottom = 20.dp)
                                )
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
        if (macHex.size != 6) {
            Log.e("DecodingQr", "Fked Up: Invalid Qr Code")
            throw Error("Invalid QR Code")
        }
        val mac: MacAddress = MacAddress.fromBytes(macHex)
        NavProvider.addLogEntry("Mac Address: $mac")
        try {
            val socket = Stand.connect(
                mac, context
            )
            if (socket == null) {
                throw Error("Couldn't connect to WiFi")
            }
            val standIsUnlocked = Stand.isUnlocked(socket)
            if (standIsUnlocked != userHasCycle) {
                NavProvider.addLogEntry("Stand reports that it ${if (standIsUnlocked) "does not have" else "has"} a cycle, if this is false, please report this by clicking here")
                throw Error("Stand reports that it ${if (standIsUnlocked) "does not have" else "has"} a cycle, if this is false, please report this by clicking here")
            }
            val standStatusToken = Stand.getToken(socket)
            val cloudToken = CloudFunctions.token(standStatusToken)
            val standToken = Stand.trigger(socket, cloudToken)
            CloudFunctions.putToken(standToken)
            Stand.disconnect()
        } catch (e: Error) {
            Stand.disconnect()
            throw Error(e.message)
        }
    }
}