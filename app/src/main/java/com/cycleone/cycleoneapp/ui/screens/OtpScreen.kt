package com.cycleone.cycleoneapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cycleone.cycleoneapp.ui.components.PrestyledText

class OtpScreen {
    @Composable
    public fun Create() {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            TextButton(
                onClick = {}, modifier = Modifier
                    .background(Color.Transparent)
                    .align(AbsoluteAlignment.Left)
            ) {
                Row( verticalAlignment = Alignment.CenterVertically) {
                    Text("‹", fontSize = 60.sp, style = MaterialTheme.typography.labelMedium)
                    Text(
                        "   Forgot Password",
                        fontSize = 15.sp,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            }
            Text("OTP Verification", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(vertical = 20.dp))
            Text("We sent the 4 digit code to your email.")
            val min = 4
            val sec = 59
            Text("This code will expire in ${min.toString().padStart(2, '0')}:${sec.toString().padStart(2, '0')}")
            Text("OTP code", style = MaterialTheme.typography.bodyMedium, modifier = Modifier
                .align(Alignment.Start)
                .padding(horizontal = 20.dp, vertical = 30.dp))
            PrestyledText().OTP()
            Button(onClick = {}, modifier = Modifier.align(Alignment.End).padding(horizontal = 10.dp, vertical = 30.dp)) {
                Text("Verify ❯")
            }
        }
    }

    @Composable
    @Preview
    fun prev() {
        Create()
    }
}