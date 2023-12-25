package com.cycleone.ttest2

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.cycleone.ttest2.ui.REQUEST_ENABLE_BT
import org.json.JSONObject
import java.util.UUID

class Stand {
    @Composable
    fun Connect(mac: ByteArray) {
        val context = LocalContext.current
        val launcher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission Accepted: Do something
                Log.d("Success","PERMISSION GRANTED")

            } else {
                // Permission Denied: Do something
                Log.d("Error","PERMISSION DENIED")
            }
        }
        val bluetoothManager: BluetoothManager = context.getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Log.e("Error", "Device Does not support Bluetooth")
            return
        }
        if (bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                launcher.launch(Manifest.permission.BLUETOOTH_CONNECT)
                return
            }
            Fragment().activity?.startActivityForResult(
                enableBtIntent,
                REQUEST_ENABLE_BT
            )
            bluetoothAdapter.cancelDiscovery()
            val standDevice = bluetoothAdapter.getRemoteDevice(mac)
            val bluetoothSocket = standDevice.createRfcommSocketToServiceRecord(UUID.randomUUID())
            bluetoothSocket.connect()
            bluetoothSocket.outputStream.write('G'.code)
            val resp = bluetoothSocket.inputStream.reader().readText()
            var respObj = JSONObject()
            try {
                respObj = JSONObject(resp)
            } catch (e: Exception) {
                Log.e("Error", "Could not parse the response: $resp")
                return
            }
            if (respObj["error"] as String? != null) {
                Log.e("Error", "Cycle was not found, stand returned: ${respObj["error"]}")
                return
            } else {
                Log.e("Error", "Cycle Found, tag is ${respObj["cycleTag"]}")
            }
            bluetoothSocket.outputStream.write('U'.code)
            Log.d("INFO", "Sent the message to unlock the Cycle")
        }
        }
}