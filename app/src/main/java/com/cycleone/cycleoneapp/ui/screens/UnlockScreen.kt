package com.cycleone.cycleoneapp.ui.screens

import android.net.MacAddress
import android.util.Log
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
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import okio.ByteString.Companion.decodeBase64

class UnlockScreen {
    @Composable
    fun Create() {
        var shouldScanQr by remember {
            mutableStateOf(false)
        }
        var canScanQr by remember {
            mutableStateOf(true)
        }
        var st by remember {
            mutableStateOf(Stand(onUnavailable = {canScanQr = false}, onAttach = {canScanQr = true}, onAttachFailure = {canScanQr = false}))
        }
        var tryUnlock by remember {
            mutableStateOf(false)
        }
        val context = LocalContext.current
        if (shouldScanQr){
            shouldScanQr = false
            QrCode.create(onSuccess = {code -> Stand})
        }
        val navController = NavProvider.controller
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                TextButton(
                    onClick = {navController.popBackStack()}, modifier = Modifier
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
                    if (canScanQr) {
                        QrCode.appContext = context
                        QrCode.create { qrCode ->
                            Stand.Connect(MacAddress.fromBytes(qrCode))
                            var resp = Stand.GetStatus()
                            if (resp != null) {
                                when(resp) {
                                    is Response.Ok -> {canScanQr = !resp.isUnlocked
                                    if (resp.cycleId != null) {
                                        CloudFunctions.Token(resp.cycleId!!)?.let {
                                            FirebaseAuth.getInstance().currentUser?.uid?.let { it1 ->
                                                resp = Stand.Unlock(
                                                    it1, it
                                                )
                                            }
                                        }
                                    }}

                                    is Response.Err -> Log.e("StandError", resp.toString())
                                }



                        }
                    }
                }}, enabled = canScanQr) {
                    Text("Scan  ")
                    Icon(Icons.Default.Search, "QR")
                }
                Text("Cycle not being used", modifier = Modifier.padding(top = 20.dp))
                Text("Time since last unlock: ")
            }
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp), horizontalArrangement = Arrangement.SpaceBetween) {
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