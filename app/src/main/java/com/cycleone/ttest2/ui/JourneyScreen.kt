package com.cycleone.ttest2.ui

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.cycleone.ttest2.Barcode
import com.cycleone.ttest2.Stand

class JourneyScreen(private val controller: NavController) {
    @Composable
    fun Screen() {
        val context = LocalContext.current
        var macAddr by rememberSaveable {
            mutableStateOf(ByteArray(0))
        }
    Barcode().getMac(context) { mac: ByteArray ->
        if (mac.size == 6) {
            macAddr = mac;
        } else {
            controller.navigate("/dashboard")
        }
    }
        Stand().Connect(macAddr);
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceEvenly){ Button(onClick = { Log.d("INFO","Ended Journey")
            controller.popBackStack()
        }) {
            Text(text = "End Journey")
        }}
    }
}