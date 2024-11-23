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
import androidx.navigation.NavController
import com.cycleone.cycleoneapp.R
import com.cycleone.cycleoneapp.services.CloudFunctions
import com.cycleone.cycleoneapp.services.NavProvider
import com.cycleone.cycleoneapp.services.QrCode
import com.cycleone.cycleoneapp.services.Response
import com.cycleone.cycleoneapp.services.Stand
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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
        navController: NavController = NavProvider.controller
    ) {

        val uid by remember {
            mutableStateOf(Firebase.auth.uid)
        }
        if (uid == null) {
            NavProvider.controller.navigate("/sign_in")
            return
        }
        var userHasCycle: Boolean? by remember {
            mutableStateOf(
                runBlocking {
                    val a = (Firebase.firestore.collection("users").document(uid!!).get()
                        .await().data?.get("HasCycle")) as Boolean
                    Log.d("HasCycle", a.toString())
                    a
                }

            )
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
            onScanSuccess = { qr ->
                showCamera = false
                if (transactionRunning > 0) {
                    return@UI
                }
                transactionRunning++
                transactionRunningLocal = transactionRunning
                userHasCycle = (Firebase.firestore.collection("users").document(uid!!).get()
                    .await().data?.get("HasCycle") ?: true) as Boolean
                CoroutineScope(Dispatchers.Main).launch {
                    if (transactionRunning > 1) {
                        return@launch
                    }
                    transactionRunning++
                    transactionRunningLocal = transactionRunning
                    if (userHasCycle == null) {
                        return@launch
                    }
                    if (userHasCycle != false) {
                        returnSequence(
                            qr,
                            context,
                            { t ->
                                if (t) transactionRunning++
                                else transactionRunning--

                                transactionRunningLocal = transactionRunning
                                Firebase.firestore.collection("users").document(uid!!).get()
                                    .addOnSuccessListener { snap ->
                                        if (snap.data?.get("HasCycle") != null) {
                                            userHasCycle = snap.data?.get("HasCycle")!! as Boolean
                                        }
                                    }
                            })
                    } else {
                        unlockSequence(
                            qr,
                            context,
                            { t ->
                                if (t) transactionRunning++
                                else transactionRunning--

                                transactionRunningLocal = transactionRunning
                                Firebase.firestore.collection("users").document(uid!!).get()
                                    .addOnSuccessListener { snap ->
                                        if (snap.data?.get("HasCycle") != null) {
                                            userHasCycle = snap.data?.get("HasCycle")!! as Boolean
                                        }
                                    }
                            }
                        )
                    }
                    transactionRunning--
                    transactionRunningLocal = transactionRunning
                    Firebase.firestore.collection("users").document(uid!!).get()
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
            buttonText = if (userHasCycle == true) "Return" else "Scan"
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
        buttonText: String = "Scan",
        transactionRunning: Int = 0,
    ) {

        val lifecycleOwner = LocalLifecycleOwner.current
        Column(
            modifier = modifier.fillMaxSize(),
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
                                Text(buttonText)
                                Icon(Icons.Default.Search, "QR")
                            }
                        }
                    } else {
                        Column {
                            val textToShow = if (permissionState.shouldShowRationale) {
                                // If the user has denied the permission but the rationale can be shown,
                                // then gently explain why the app requires this permission
                                "The camera permission is required to scan the stand's QR, and the wifi permissions are required to connect to the stand"
                            } else {
                                // If it's the first time the user lands on this feature, or the user
                                // doesn't want to be asked again for this permission, explain that the
                                // permission is required
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
                }
            }
        }
    }


    @OptIn(ExperimentalStdlibApi::class)
    suspend fun returnSequence(
        qrCode: String,
        context: Context,
        onTransactionChange: (Boolean) -> Unit
    ) {
        try {
            if (transactionRunning > 2) {
                CoroutineScope(Dispatchers.Main).launch {
                    NavProvider.snackbarHostState.showSnackbar("Some process is already running")
                }
                return
            }
            Log.d("qrCode", qrCode)
            val macHex = decodeHexFromStr(qrCode.trim())
            Log.d("QrSize", macHex.size.toString())
            if (macHex.size != 6) {
                Log.e("DecodingQr", "Fked Up: Invalid Qr Code")
                return
            }
            val mac: MacAddress = MacAddress.fromBytes(macHex)
            print(mac)
            CoroutineScope(Dispatchers.Main).launch {
                NavProvider.snackbarHostState.showSnackbar("Mac Address: $mac")
            }
            Stand.connect(
                mac, context, onError = { it ->
                    CoroutineScope(Dispatchers.Main).launch {
                        NavProvider.snackbarHostState.showSnackbar(it)
                    }
                }
            ) { socket ->
                onTransactionChange(true)
                try {
                    if (transactionRunning > 3) {
                        return@connect
                    }
                    CoroutineScope(Dispatchers.Main).launch {
                        NavProvider.snackbarHostState.showSnackbar("WiFi Connection made")
                    }
                    val token = CloudFunctions.token(Stand.getToken(socket))
                    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@connect
                    Log.d("Uid", uid)
                    Log.d("serverResp", token.toHexString())
                    Log.d("Stand", "Connecting")
                    val resp = Stand.returnCmd(socket, uid, token)
                    CoroutineScope(Dispatchers.Main).launch {
                        NavProvider.snackbarHostState.showSnackbar("Return Command completed")
                    }
                    print(resp)
                    CoroutineScope(Dispatchers.Main).launch {
                        NavProvider.snackbarHostState.showSnackbar("Getting the token from Stand again")
                    }
                    val newToken = Stand.getToken(socket)
                    CoroutineScope(Dispatchers.Main).launch {
                        NavProvider.snackbarHostState.showSnackbar("Sending the token back to the server")
                    }
                    CloudFunctions.putToken(newToken)
                    CoroutineScope(Dispatchers.Main).launch {
                        NavProvider.snackbarHostState.showSnackbar("Token sent")
                    }
                    Stand.disconnect()
                } catch (e: Throwable) {
                    CoroutineScope(Dispatchers.Main).launch {
                        NavProvider.snackbarHostState.showSnackbar("Err: $e")
                    }
                    Log.e("ReturnSeq", e.toString())
                    Log.e("ReturnSeq", e.stackTraceToString())
                } finally {
                    onTransactionChange(false)
                }
            }
        } catch (e: InvalidParameterException) {
            CoroutineScope(Dispatchers.Main).launch {
                NavProvider.snackbarHostState.showSnackbar("Invalid QR")
            }
        } catch (e: NumberFormatException) {
            CoroutineScope(Dispatchers.Main).launch {
                NavProvider.snackbarHostState.showSnackbar("Invalid QR")

            }
        } catch (e: Throwable) {
            CoroutineScope(Dispatchers.Main).launch {
                NavProvider.snackbarHostState.showSnackbar("Err: $e")
            }
            Log.e("ReturnSeqOuter", e.toString())
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    suspend fun unlockSequence(
        qrCode: String,
        context: Context,
        onTransactionChange: (Boolean) -> Unit
    ) {
        try {
            if (transactionRunning > 2) {
                CoroutineScope(Dispatchers.Main).launch {
                    NavProvider.snackbarHostState.showSnackbar("Some process is already running")
                }
                return
            }
            Log.d("qrCode", qrCode)
            val macHex = decodeHexFromStr(qrCode)
            Log.d("QrSize", macHex.size.toString())
            if (macHex.size != 6) {
                Log.e("DecodingQr", "Fked Up: Invalid Qr Code")
                return
            }
            val mac: MacAddress = MacAddress.fromBytes(macHex)
            print(mac)
            CoroutineScope(Dispatchers.Main).launch {
                NavProvider.snackbarHostState.showSnackbar("Mac Address: $mac")
            }
            Stand.connect(
                mac, context, onError = {
                    CoroutineScope(Dispatchers.Main).launch {
                        NavProvider.snackbarHostState.showSnackbar(it)
                    }
                }

            ) { socket ->
                onTransactionChange(true)
                try {
                    if (transactionRunning > 3) {
                        return@connect
                    }
                    CoroutineScope(Dispatchers.Main).launch {
                        NavProvider.snackbarHostState.showSnackbar("WiFi Connection made")
                    }
                    Log.d("Stand", "Connecting")
                    var resp = Stand.getStatus(socket)
                    CoroutineScope(Dispatchers.Main).launch {
                        NavProvider.snackbarHostState.showSnackbar("Stand Status: ${resp}")
                    }
                    val standToken = Stand.getToken(socket)
                    print(resp)
                    if (resp == null) {
                        return@connect
                    }
                    when (resp) {
                        is Response.Ok -> {
                            if (resp.cycleId == null) {
                                CloudFunctions.putToken(standToken)
                                CoroutineScope(Dispatchers.Main).launch {
                                    NavProvider.snackbarHostState.showSnackbar("Data updated in server")
                                }
                            }
                            val cycleId = resp.cycleId
                            Log.d(
                                "CycleId Before Function Call", cycleId.toString()
                            )
                            CloudFunctions.token(standToken).let {
                                CoroutineScope(Dispatchers.Main).launch {
                                    NavProvider.snackbarHostState.showSnackbar("Unlock token received from backend")
                                }
                                FirebaseAuth.getInstance().currentUser?.uid?.let { it1 ->
                                    Log.d("Uid", it1)
                                    Log.d("serverResp", it.toHexString())
                                    resp = Stand.unlock(
                                        socket, it1, it
                                    )
                                    CoroutineScope(Dispatchers.Main).launch {
                                        NavProvider.snackbarHostState.showSnackbar("Stand Unlocked")
                                    }
                                    Log.d("StandResp", resp.toString())
                                }
                            }
                        }

                        is Response.Err -> {
                            CoroutineScope(Dispatchers.Main).launch {
                                NavProvider.snackbarHostState.showSnackbar("Err: ${resp.toString()}")
                            }
                            Log.e(
                                "StandError", resp.toString()
                            )
                        }

                    }
                } catch (e: Throwable) {
                    CoroutineScope(Dispatchers.Main).launch {
                        NavProvider.snackbarHostState.showSnackbar("Err: $e")
                    }
                    Log.e("UnlockSeq", e.toString())
                } finally {
                    onTransactionChange(false)
                }
            }
        } catch (e: Throwable) {
            CoroutineScope(Dispatchers.Main).launch {
                NavProvider.snackbarHostState.showSnackbar("Err: $e")
            }
            Log.e("UnlockSeq", e.toString())
        }
    }
}