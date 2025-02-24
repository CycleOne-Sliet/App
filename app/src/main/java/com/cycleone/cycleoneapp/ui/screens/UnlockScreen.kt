package com.cycleone.cycleoneapp.ui.screens

import android.Manifest
import android.content.Context
import android.net.MacAddress
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cycleone.cycleoneapp.R
import com.cycleone.cycleoneapp.services.CloudFunctions
import com.cycleone.cycleoneapp.services.NavProvider
import com.cycleone.cycleoneapp.services.QrCode
import com.cycleone.cycleoneapp.services.Stand
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
    ) {

        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid
        if (uid == null) {
            NavProvider.controller.navigate("/sign_in")
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
        var transactionRunningLocal by remember {
            mutableStateOf(transactionRunning)
        }
        UI(
            modifier, transactionRunning = transactionRunningLocal,
            user = user,
            onScanSuccess = { qr ->
                showCamera = false
                if (transactionRunning > 0) {
                    return@UI
                }
                transactionRunning++
                transactionRunningLocal = transactionRunning
                Log.d("onScanSuccess", "Fetching Cycle Status from backend")
                userHasCycle = (Firebase.firestore.collection("users").document(uid).get()
                    .await().data?.get("HasCycle") ?: true) as Boolean
                Log.d("onScanSuccess", "Cycle Status: ${userHasCycle}")
                CoroutineScope(Dispatchers.Main).launch {
                    if (transactionRunning > 1) {
                        return@launch
                    }
                    transactionRunning++
                    transactionRunningLocal = transactionRunning
                    if (userHasCycle == null) {
                        return@launch
                    }
                    triggerStandSeq(
                        qr,
                        context, userHasCycle
                    ) { t ->
                        if (t) transactionRunning++
                        else transactionRunning--

                        transactionRunningLocal = transactionRunning
                        Firebase.firestore.collection("users").document(uid).get()
                            .addOnSuccessListener { snap ->
                                if (snap.data?.get("HasCycle") != null) {
                                    userHasCycle = snap.data?.get("HasCycle")!! as Boolean
                                }
                            }
                    }
                    transactionRunning--
                    transactionRunningLocal = transactionRunning
                    Firebase.firestore.collection("users").document(uid).get()
                        .addOnSuccessListener { snap ->
                            if (snap.data?.get("HasCycle") != null) {
                                userHasCycle = snap.data?.get("HasCycle")!! as Boolean
                            }
                        }
                }
                transactionRunning--
                transactionRunningLocal = transactionRunning
            },
            showCamera = showCamera,
            permissionState = permissionStates,
            buttonClick = {
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
        buttonClick: () -> Unit = {},
        transactionRunning: Int = 0,
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
        val lifecycleOwner = LocalLifecycleOwner.current
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (showCamera) {
                    QrCode.startCamera(lifecycleOwner = lifecycleOwner, onSuccess = onScanSuccess)
                } else {
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
                                Button(onClick = {
                                    buttonClick()
                                }) {
                                    if (userHasCycle == true) {
                                        Text("Scan to return cycle")
                                    }
                                    if (userHasCycle != true) {
                                        Text("Scan to unlock cycle")
                                    }
                                    Icon(Icons.Default.Search, "QR")
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

    fun triggerStandSeq(
        qrCode: String,
        context: Context,
        userHasCycle: Boolean?,
        onTransactionChange: (Boolean) -> Unit
    ) {
        try {
            if (transactionRunning > 2) {
                NavProvider.addLogEntry("Some process is already running")
                return
            }
            val macHex = decodeHexFromStr(qrCode.trim())
            if (macHex.size != 6) {
                Log.e("DecodingQr", "Fked Up: Invalid Qr Code")
                return
            }
            val mac: MacAddress = MacAddress.fromBytes(macHex)
            print(mac)
            NavProvider.addLogEntry("Mac Address: $mac")
            Stand.connect(
                mac, context, onUnavailable = {
                    NavProvider.addLogEntry("Stand Not Available")
                    Stand.disconnect()
                }, onBlocked = {
                    NavProvider.addLogEntry("WiFi seems to be blocked")
                    Stand.disconnect()
                }, onLost = {
                    NavProvider.addLogEntry("WiFi Disconnected")
                    Stand.disconnect()
                }
            ) { socket ->
                onTransactionChange(true)
                try {
                    if (transactionRunning > 3) {
                        throw Throwable("Transactions Already Running")
                    }
                    val standIsUnlocked = Stand.isUnlocked(socket)
                    if (standIsUnlocked != userHasCycle) {
                        NavProvider.addLogEntry("Stand reports that it ${if (standIsUnlocked) "does not have" else "has"} a cycle, if this is false, please report this by clicking here")
                        throw Throwable("Invalid stand state")
                    }
                    NavProvider.addLogEntry("WiFi Connection made")
                    val standStatusToken = Stand.getToken(socket)
                    NavProvider.addLogEntry(
                        "StandStatusToken Received"
                    )
                    NavProvider.addLogEntry(
                        "StandStatusToken Len: ${
                            standStatusToken.size
                        }"
                    )
                    val cloudToken = CloudFunctions.token(standStatusToken)
                    NavProvider.addLogEntry(
                        "Cloud Function Token Received"
                    )
                    NavProvider.addLogEntry(
                        "Cloud Function Token Len: ${
                            standStatusToken.size
                        }"
                    )
                    val standToken = Stand.trigger(socket, cloudToken)
                    NavProvider.addLogEntry(
                        "Stand Triggered"
                    )
                    NavProvider.addLogEntry(
                        "Stand Token Size: ${
                            standToken.size
                        }"
                    )
                    CloudFunctions.putToken(standToken)
                    NavProvider.addLogEntry("Done")
                    Stand.disconnect()
                } catch (e: Throwable) {
                    NavProvider.addLogEntry("Err: $e")
                    Log.e("ReturnSeq", e.toString())
                    Log.e("ReturnSeq", e.stackTraceToString())
                } finally {
                    Stand.disconnect()
                    onTransactionChange(false)
                }
            }
        } catch (e: InvalidParameterException) {
            NavProvider.addLogEntry("Invalid QR")
        } catch (e: NumberFormatException) {
            NavProvider.addLogEntry("Invalid QR")

        } catch (e: Throwable) {
            NavProvider.addLogEntry("Err: $e")
            Log.e("ReturnSeqOuter", e.toString())
        }
    }

}
