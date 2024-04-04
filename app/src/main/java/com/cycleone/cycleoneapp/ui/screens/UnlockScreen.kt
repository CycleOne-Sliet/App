package com.cycleone.cycleoneapp.ui.screens

import android.Manifest
import android.net.MacAddress
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cycleone.cycleoneapp.R
import com.cycleone.cycleoneapp.services.CloudFunctions
import com.cycleone.cycleoneapp.services.NavProvider
import com.cycleone.cycleoneapp.services.QrCode
import com.cycleone.cycleoneapp.services.Response
import com.cycleone.cycleoneapp.services.Stand
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.net.Socket
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
                Stand(
                    onUnavailable = { canScanQr = false },
                    onAttach = { canScanQr = true },
                    onAttachFailure = { canScanQr = false })
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
                TextButton(
                    onClick = { navController.popBackStack() }, modifier = Modifier
                        .background(Color.Transparent)
                        .align(AbsoluteAlignment.Left)
                ) {
                    Text("‹", fontSize = 50.sp, style = MaterialTheme.typography.titleLarge)
                }
                Image(painter = painterResource(id = R.drawable.unlock_image), "Unlock Image")
                Text(
                    "Scan QR Code to \nUnlock",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 20.dp)
                )
                Button(onClick = {
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
                    shouldScanQr =
                        cameraPermissionState.status.isGranted && wifiPermissionState.allPermissionsGranted
                }, enabled = canScanQr) {
                    Text("Scan  ")
                    Icon(Icons.Default.Search, "QR")
                }
                Text("Cycle not being used", modifier = Modifier.padding(top = 20.dp))
                Text("Time since last unlock: ")

                if (shouldScanQr && canScanQr) {
                    QrCode.startCamera(lifecycleOwner = lifecycleOwner, onSuccess = { qrCode ->
                        runBlocking {
                            if (startedQrScanning) {
                                return@runBlocking
                            }
                            startedQrScanning = true
                            launch {
                                delay(3000)
                                startedQrScanning = false
                            }

                            Log.d("qrCode", qrCode)
                            Stand.appContext = context
                            shouldScanQr = false
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
                            ) { socket: Socket ->
                                if (startedConnecting) {
                                    return@Connect
                                }
                                startedConnecting = true
                                launch {
                                    delay(2000)
                                    startedConnecting = false
                                }
                                Log.d("Stand", "Connecting")
                                var resp = Stand.GetStatus(socket)
                                print(resp)
                                // This stair makes me want to touch an actual, live Jacob's ladder
                                if (resp != null) {
                                    when (resp) {
                                        is Response.Ok -> {
                                            Toast.makeText(
                                                context,
                                                "${resp.cycleId} : ${resp.isUnlocked}",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            canScanQr = !resp.isUnlocked
                                            if (resp.cycleId != null) {
                                                val cycleId = resp.cycleId
                                                runBlocking {
                                                    Log.d(
                                                        "CycleId Before Function Call",
                                                        cycleId.toString()
                                                    )
                                                    CloudFunctions.Token(cycleId!!)?.let {
                                                        FirebaseAuth.getInstance().currentUser?.uid?.let { it1 ->
                                                            Log.d("Uid", it1)
                                                            Log.d("serverResp", it.toHexString())
                                                            resp = Stand.Unlock(
                                                                socket,
                                                                it1, it
                                                            )
                                                            Toast.makeText(
                                                                context,
                                                                "Successfully Unlocked the stand",
                                                                Toast.LENGTH_LONG
                                                            ).show()
                                                            startedQrScanning = false
                                                            startedConnecting = false
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        is Response.Err -> {
                                            Log.e(
                                                "StandError",
                                                resp.toString()
                                            )
                                            Toast.makeText(
                                                context,
                                                (resp as Response.Err).error,
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                }
                            }
                        }
                    }
                    )
                }

            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(onClick = {}) {
                    Icon(Icons.Default.Person, "Profile")
                }
                OutlinedButton(onClick = {}) {
                    Icon(Icons.Default.Home, "Home")
                }
                OutlinedButton(onClick = {}) {
                    Icon(Icons.Default.Notifications, "Notifications")
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