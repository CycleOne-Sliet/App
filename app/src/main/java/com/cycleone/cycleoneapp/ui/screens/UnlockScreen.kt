package com.cycleone.cycleoneapp.ui.screens

import android.Manifest
import android.content.Context
import android.net.MacAddress
import android.net.Network
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
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
import com.cycleone.cycleoneapp.R
import com.cycleone.cycleoneapp.services.CloudFunctions
import com.cycleone.cycleoneapp.services.NavProvider
import com.cycleone.cycleoneapp.services.QrCode
import com.cycleone.cycleoneapp.services.Response
import com.cycleone.cycleoneapp.services.Stand
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
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

class UnlockScreen {
    @OptIn(ExperimentalStdlibApi::class, ExperimentalPermissionsApi::class)
    @Composable
    fun Create() {
        var shouldScanQr by remember {
            mutableStateOf(false)
        }
        var canScanQr by remember {
            mutableStateOf(true)
        }
        var st by remember {
            mutableStateOf(
                Stand
            )
        }
        var tryUnlock by remember {
            mutableStateOf(false)
        }
        var startedQrScanning by remember {
            mutableStateOf(false)
        }
        var startedConnecting by remember {
            mutableStateOf(false)
        }
        var unlocking by remember {
            mutableStateOf(false)
        }

        var returning by remember {
            mutableStateOf(false)
        }
        val userHasCycle = runBlocking {
            Log.d("UID", FirebaseAuth.getInstance().uid!!.toString())
            Log.d(
                "User",
                Firebase.firestore.collection("users").document(FirebaseAuth.getInstance().uid!!)
                    .get()
                    .await()!!.data.toString()
            )
            Firebase.firestore.collection("users").document(FirebaseAuth.getInstance().uid!!).get()
                .await()!!.data?.get("hasCycle") as Boolean

        }
        val wifiPermissionState = rememberMultiplePermissionsState(
            listOf(Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE)
        )

        val cameraPermissionState = rememberPermissionState(
            Manifest.permission.CAMERA
        )
        val lifecycleOwner = LocalLifecycleOwner.current
        val context = LocalContext.current
        val navController = NavProvider.controller
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (shouldScanQr && canScanQr) {
                    QrCode.startCamera(lifecycleOwner = lifecycleOwner, onSuccess = {
                        if (startedQrScanning) {
                            return@startCamera
                        }
                        startedQrScanning = true
                        if (unlocking) {
                            unlockSequence(it, context)
                            unlocking = false
                        } else if (returning) {
                            returnSequence(it, context)
                            returning = false
                        }
                        startedQrScanning = false
                        shouldScanQr = false
                    })
                } else {
                    Image(painter = painterResource(id = R.drawable.unlock_image), "Unlock Image")
                    Text(
                        "Scan QR Code to \nUnlock",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )
                    if (userHasCycle) {
                        Button(onClick = {
                            shouldScanQr = camAndWifiPermissions(
                                cameraPermissionState,
                                wifiPermissionState,
                                context
                            )
                            returning = true
                        }) {
                            Text("Return  ")
                            Icon(Icons.Default.Search, "QR")
                        }
                    } else {
                        Button(onClick = {
                            shouldScanQr = camAndWifiPermissions(
                                cameraPermissionState,
                                wifiPermissionState,
                                context
                            )
                            unlocking = true
                        }, enabled = canScanQr) {
                            Text("Scan  ")
                            Icon(Icons.Default.Search, "QR")
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalPermissionsApi::class)
    fun camAndWifiPermissions(
        cameraPermissionState: PermissionState,
        wifiPermissionState: MultiplePermissionsState,
        context: Context
    ): Boolean {

        cameraPermissionState.launchPermissionRequest()
        if (!cameraPermissionState.status.isGranted) {
            if (cameraPermissionState.status.shouldShowRationale) {
                Toast.makeText(
                    context,
                    "Camera permission is needed to scan\nthe qr codes of stand",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    context,
                    "Please grant the permission for camera in the settings",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        wifiPermissionState.launchMultiplePermissionRequest()
        if (!wifiPermissionState.allPermissionsGranted) {
            if (wifiPermissionState.shouldShowRationale) {
                Toast.makeText(
                    context,
                    "Wifi permission is needed to scan\nthe qr codes of stand",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    context,
                    "Please grant the permission for wifi in the settings",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        return cameraPermissionState.status.isGranted && wifiPermissionState.allPermissionsGranted
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun returnSequence(qrCode: String, context: Context) {
        runBlocking {
            Log.d("qrCode", qrCode)
            Stand.appContext = context
            val macHex = decodeHexFromStr(qrCode)
            Log.d("QrSize", macHex.size.toString())
            if (macHex.size != 6) {
                Log.e("DecodingQr", "Fked Up: Invalid Qr Code")
                return@runBlocking
            }
            val mac: MacAddress = MacAddress.fromBytes(macHex)
            print(mac)
            Stand.Connect(
                mac
            ) { socket: Network ->
                Log.d("Stand", "Connecting")
                var resp = Stand.GetStatus(socket)
                val standToken = Stand.GetToken(socket)
                print(resp)
                if (resp != null && standToken != null) {
                    when (resp) {
                        is Response.Ok -> {
                            Toast.makeText(
                                context, "${resp.cycleId} : ${resp.isUnlocked}", Toast.LENGTH_LONG
                            ).show()
                            if (resp.cycleId != null) {
                                val cycleId = resp.cycleId
                                runBlocking {
                                    Log.d(
                                        "CycleId Before Function Call", cycleId.toString()
                                    )
                                    CloudFunctions.PutToken(standToken)
                                }
                            }
                        }

                        is Response.Err -> {
                            Log.e(
                                "StandError", resp.toString()
                            )
                            Toast.makeText(
                                context, resp.error, Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun unlockSequence(qrCode: String, context: Context) {
        runBlocking {
            Log.d("qrCode", qrCode)
            Stand.appContext = context
            val macHex = decodeHexFromStr(qrCode)
            Log.d("QrSize", macHex.size.toString())
            if (macHex.size != 6) {
                Log.e("DecodingQr", "Fked Up: Invalid Qr Code")
                return@runBlocking
            }
            val mac: MacAddress = MacAddress.fromBytes(macHex)
            print(mac)
            Stand.Connect(
                mac
            ) { socket: Network ->
                Log.d("Stand", "Connecting")
                var resp = Stand.GetStatus(socket)
                val standToken = Stand.GetToken(socket)
                print(resp)
                if (resp != null && standToken != null) {
                    when (resp) {
                        is Response.Ok -> {
                            Toast.makeText(
                                context, "${resp.cycleId} : ${resp.isUnlocked}", Toast.LENGTH_LONG
                            ).show()
                            if (resp.cycleId != null) {
                                val cycleId = resp.cycleId
                                runBlocking {
                                    Log.d(
                                        "CycleId Before Function Call", cycleId.toString()
                                    )
                                    CloudFunctions.Token(standToken)?.let {
                                        FirebaseAuth.getInstance().currentUser?.uid?.let { it1 ->
                                            Log.d("Uid", it1)
                                            Log.d("serverResp", it.toHexString())
                                            resp = Stand.Unlock(
                                                socket, it1, it
                                            )
                                            Toast.makeText(
                                                context,
                                                "Successfully Unlocked the stand",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                }
                            }
                        }

                        is Response.Err -> {
                            Log.e(
                                "StandError", resp.toString()
                            )
                            Toast.makeText(
                                context, (resp as Response.Err).error, Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
    }

    @Composable
    @Preview
    fun Preview() {
        Create()
    }
}