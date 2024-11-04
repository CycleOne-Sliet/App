package com.cycleone.cycleoneapp.ui.screens

import android.Manifest
import android.content.Context
import android.net.MacAddress
import android.util.Log
import android.widget.Toast
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
import androidx.compose.runtime.rememberCoroutineScope
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
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
        var transactionRunning = false
        var userHasCycle = true
    }

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun Create(modifier: Modifier = Modifier) {
        var uid by remember {
            mutableStateOf(Firebase.auth.uid)
        }
        if (uid == null) {
            NavProvider.controller.navigate("/sign_in")
            return
        }
        Firebase.firestore.collection("users").document(uid!!).get().addOnSuccessListener { snap ->
            if (snap.data?.get("HasCycle") != null) {
                userHasCycle = snap.data?.get("HasCycle")!! as Boolean
            }
        }
        Log.d("UID", uid.toString())
        var showCamera by remember {
            mutableStateOf(false)
        }
        val scope = rememberCoroutineScope()
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
        UI(modifier, onScanSuccess = { qr ->
            scope.launch {
                showCamera = false
                Log.d("QR Scanned", qr)
                Log.d("userHasCycle", userHasCycle.toString())
                if (userHasCycle) {
                    returnSequence(qr, context)
                } else {
                    unlockSequence(qr, context)
                }
            }
            Firebase.firestore.collection("users").document(uid!!).get()
                .addOnSuccessListener { snap ->
                    if (snap.data?.get("HasCycle") != null) {
                        userHasCycle = snap.data?.get("HasCycle")!! as Boolean
                    }
                }
        }, showCamera = showCamera, permissionState = permissionStates, buttonClick = {
            showCamera = true
        }, buttonText = if (userHasCycle) "Return" else "Scan")
    }

    @Preview
    @Composable
    fun UI(
        modifier: Modifier = Modifier,
        onScanSuccess: (String) -> Unit = {},
        permissionState: MultiplePermissionsState = rememberMultiplePermissionsState(listOf()),
        showCamera: Boolean = false,
        buttonClick: () -> Unit = {},
        buttonText: String = "Scan",
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
                        if (transactionRunning) {
                            CircularProgressIndicator(
                                modifier = Modifier.width(64.dp),
                            )
                        } else {
                            Text(
                                "Scan QR Code to \nUnlock",
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(bottom = 20.dp)
                            )
                            Button(enabled = !transactionRunning, onClick = {
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


    @OptIn(ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class)
    suspend fun returnSequence(qrCode: String, context: Context) {
        try {
            if (transactionRunning) {
                return
            }
            transactionRunning = true
            Log.d("qrCode", qrCode)
            Stand.appContext = context
            val macHex = decodeHexFromStr(qrCode.trim())
            Log.d("QrSize", macHex.size.toString())
            if (macHex.size != 6) {
                Log.e("DecodingQr", "Fked Up: Invalid Qr Code")
                return
            }
            val mac: MacAddress = MacAddress.fromBytes(macHex)
            print(mac)
            val socket = Stand.Connect(
                mac
            ) ?: return

            Log.d("Stand", "Connecting")
            var resp = Stand.GetStatus(socket)
            val standToken = Stand.GetToken(socket)
            print(resp)
            if (resp == null) {
                return
            }
            when (resp) {
                is Response.Ok -> {
                    Toast.makeText(
                        context, "${resp.cycleId} : ${resp.isUnlocked}", Toast.LENGTH_LONG
                    ).show()
                    val token = CloudFunctions.Token(standToken)
                    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
                    Log.d("Uid", uid)
                    Log.d("serverResp", token.toHexString())
                    resp = Stand.Unlock(
                        socket, uid, token
                    )
                    Log.d("StandResp", resp.toString())
                    Toast.makeText(
                        context, "Successfully Unlocked the stand", Toast.LENGTH_LONG
                    ).show()
                    Toast.makeText(
                        context, "Waiting for cycle to be put into the stand", Toast.LENGTH_LONG
                    ).show()
                    var attempts = 10
                    while (attempts > 0) {
                        attempts--
                        val status = Stand.GetStatus(socket) ?: (attempts++)
                        when (status) {
                            is Response.Ok -> {
                                if (status.cycleId == null) {
                                    continue
                                }
                                val token = Stand.GetToken(socket)
                                CloudFunctions.PutToken(token)
                            }

                            is Response.Err -> {
                                Log.e(
                                    "StandError", resp.toString()
                                )
                                Toast.makeText(
                                    context,
                                    (resp as Response.Err).error,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                        delay(1000L)
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
        } finally {
            transactionRunning = false
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    suspend fun unlockSequence(qrCode: String, context: Context) {
        try {
            if (transactionRunning) {
                return
            }
            transactionRunning = true
            Log.d("qrCode", qrCode)
            Stand.appContext = context
            val macHex = decodeHexFromStr(qrCode)
            Log.d("QrSize", macHex.size.toString())
            if (macHex.size != 6) {
                Log.e("DecodingQr", "Fked Up: Invalid Qr Code")
                return
            }
            val mac: MacAddress = MacAddress.fromBytes(macHex)
            print(mac)
            val socket = Stand.Connect(
                mac
            ) ?: return
            Log.d("Stand", "Connecting")
            var resp = Stand.GetStatus(socket)
            val standToken = Stand.GetToken(socket)
            print(resp)
            if (resp == null) {
                return
            }
            when (resp) {
                is Response.Ok -> {
                    Toast.makeText(
                        context, "${resp.cycleId} : ${resp.isUnlocked}", Toast.LENGTH_LONG
                    ).show()
                    if (resp.cycleId == null) {
                        CloudFunctions.PutToken(standToken)
                    }
                    val cycleId = resp.cycleId
                    Log.d(
                        "CycleId Before Function Call", cycleId.toString()
                    )
                    CloudFunctions.Token(standToken).let {
                        FirebaseAuth.getInstance().currentUser?.uid?.let { it1 ->
                            Log.d("Uid", it1)
                            Log.d("serverResp", it.toHexString())
                            resp = Stand.Unlock(
                                socket, it1, it
                            )
                            Log.d("StandResp", resp.toString())
                            Toast.makeText(
                                context, "Successfully Unlocked the stand", Toast.LENGTH_LONG
                            ).show()
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
        } finally {
            transactionRunning = false
        }
    }
}
