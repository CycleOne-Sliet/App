package com.cycleone.cycleoneapp.services

import android.app.Activity
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.MacAddress
import android.net.Network
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.net.NetworkRequest
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.squareup.moshi.FromJson
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.adapter
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import okio.ByteString.Companion.readByteString
import java.io.ByteArrayOutputStream
import java.lang.reflect.Method


// Class representing the various commands that can be sent to the Stand
sealed class Command {
    // self explanatory
    // Unlocks the stand
    data class Unlock(val user: String, val serverRespToken: ByteArray) : Command() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Unlock

            if (user != other.user) return false
            return serverRespToken.contentEquals(other.serverRespToken)
        }

        override fun hashCode(): Int {
            var result = user.hashCode()
            result = 31 * result + serverRespToken.contentHashCode()
            return result
        }
    }

    // get the cycle_id and the lock status of the stand
    class GetStatus : Command()

    // Returns the size of the Command, in bytes
    fun getEncodedSize(): Int {
        return when (this) {
            is Unlock -> user.toByteArray().size + serverRespToken.size + 2
            is GetStatus -> 0
        }
    }

    // Returns the byte representation of the command
    // Suitable to be sent to the stand
    fun getData(): ByteArray {
        val dataWriter = ByteArrayOutputStream(this.getEncodedSize())
        when (this) {
            is Unlock -> {
                val userBytes = user.toByteArray()
                // Put the length of user id as the first byte
                dataWriter.write(userBytes.size)
                // Put the data of the user id after it
                dataWriter.write(userBytes)
                // Put the length of the server token after that
                dataWriter.write(serverRespToken.size)
                // Put the data of the server token after that
                dataWriter.write(serverRespToken)
                return dataWriter.toByteArray()
            }

            is GetStatus -> {
            }
        }
        return dataWriter.toByteArray()
    }
}

// Responses that the stand can send
// Self Explanatory
sealed class Response {
    data class Ok(val isUnlocked: Boolean, val cycleId: String?) : Response()
    data class Err(val error: String) : Response()
}

data class ResponseJson(val isUnlocked: Boolean?, val cycleId: String?, val error: String?)

// Adaptor for parsing the json returned from stand
// If clarification needed, go to the moshi documentation on github
class ResponseAdapter {
    @ToJson
    fun toJson(resp: Response): ResponseJson {
        return when (resp) {
            is Response.Ok -> ResponseJson(resp.isUnlocked, resp.cycleId, null)
            is Response.Err -> ResponseJson(null, null, resp.error)
        }
    }

    @FromJson
    fun fromJson(response: ResponseJson): Response {
        return if (response.isUnlocked != null) {
            Response.Ok(
                isUnlocked = response.isUnlocked,
                cycleId = response.cycleId
            )
        } else if (response.error != null) {
            Response.Err(response.error)
        } else {
            Response.Err("Json does contain the required fields")
        }
    }
}

@OptIn(ExperimentalStdlibApi::class)
class Stand : Application() {
    companion object {
        // preserving state between function calls
        // Also making sure that there is only one connection to the stand at any given time
        lateinit var appContext: Context
        lateinit var connectivityManager: ConnectivityManager
        lateinit var networkCallback: NetworkCallback

        @Volatile
        var IsConnected = false
        val parser = Moshi.Builder().add(ResponseAdapter())
            .add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory()).build()
            .adapter<Response>()

        // Disconnecting from the stand
        // Should be called as soon as the exchange between stand and mobile has occurred
        // Frees up the wifi
        fun disconnect() {
            connectivityManager.unregisterNetworkCallback(networkCallback)
            runBlocking {
                NavProvider.addLogEntry(
                    "Stand Disconnect Request sent",
                )
            }
        }

        fun getToken(network: Network): ByteArray {
            // Establish an Http Connection
            val socket = network.socketFactory.createSocket("10.10.10.10", 80)
            val outputStream = socket.getOutputStream()
            outputStream.write(ByteArray(1) { 'U'.code.toByte() })
            outputStream.flush()
            val inputStream = socket.getInputStream()
            val isError = inputStream.read()
            if (isError == 1) {
                val errorLength = inputStream.read()
                val error = inputStream.readByteString(errorLength)
                throw Throwable(error.toString())
            }

            // Check for any errors
            // Read the body
            val resp = ByteArray(40)
            val readBytes = inputStream.read(resp)
            if (readBytes != 40) {
                throw Throwable("Invalid number of bytes read")
            }
            socket.close()
            return resp
        }


        // Connects to the stand over the mac address
        fun connect(
            mac: MacAddress,
            context: Context,
            onUnavailable: () -> Unit,
            onLost: () -> Unit,
            onBlocked: () -> Unit,
            onConnect: suspend (Network) -> Unit,
        ) {
            // Used to configure the wifi network
            // We are setting the Ssid to CycleOneS1
            // Password to CycleOne and mac address to whatever that was passed in
            this.appContext = context
            connectivityManager =
                appContext.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val wifiManager: WifiManager =
                context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            if (wifiManager.wifiState != WifiManager.WIFI_STATE_ENABLED) {
                startActivityForResult(
                    context as Activity,
                    Intent(Settings.ACTION_WIFI_SETTINGS),
                    6,
                    null
                )
            }
            val wifiNetworkSpecifier =
                WifiNetworkSpecifier.Builder().setWpa2Passphrase("CycleOne")
                    .setBssid(mac).setSsid("CycleOneS1").setIsHiddenSsid(true)
                    .build()
            // Configuring the request for wifi from the android
            // We are specifying that we need wifi and we don't want to route internet over that
            // wifi
            val networkRequest = NetworkRequest.Builder().addTransportType(TRANSPORT_WIFI)
                .setNetworkSpecifier(wifiNetworkSpecifier).removeCapability(
                    NET_CAPABILITY_INTERNET
                ).build()
            // Callbacks for when network state changes
            networkCallback = object : NetworkCallback() {
                // When network becomes available, call the onConnect function, and then disconnect
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    if (IsConnected) {
                        return
                    }
                    IsConnected = true
                    runBlocking {
                        onConnect(network)
                    }
                    IsConnected = false
                }

                override fun onUnavailable() {
                    super.onUnavailable()
                    onUnavailable()
                    Log.e("Unavailable", "Network is not found")
                    if (isHotspotActive(context)) {
                        Toast.makeText(
                            appContext,
                            "Having hotspot active can lead to issues with connection, please turn hotspot off",
                            Toast.LENGTH_LONG
                        ).show()
                        val intent = Intent(Intent.ACTION_MAIN, null)
                        intent.addCategory(Intent.CATEGORY_LAUNCHER)
                        val cn = ComponentName(
                            "com.android.settings",
                            "com.android.settings.TetherSettings"
                        )
                        intent.setComponent(cn)
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(context, intent, null)
                    } else if (isBluetoothActive(context)) {
                        Toast.makeText(
                            appContext,
                            "Having Bluetooth on can cause connection problems, please turn it off",
                            Toast.LENGTH_LONG
                        ).show()
                        val intent =
                            Intent("android.bluetooth.adapter.action.REQUEST_DISABLE")
                        startActivity(context, intent, null)
                    }
                    Toast.makeText(
                        appContext,
                        "Could not connect to stand",
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    onLost()
                    Toast.makeText(
                        appContext,
                        "Stand WiFi connection lost",
                        Toast.LENGTH_LONG
                    ).show()
                }


                // Called when the Wifi is Blocked, We send the user to Settings to unblock it
                override fun onBlockedStatusChanged(network: Network, blocked: Boolean) {
                    super.onBlockedStatusChanged(network, blocked)
                    if (blocked) {
                        onBlocked()
                        Log.e("Unavailable", "Network is Blocked")
                        Toast.makeText(
                            appContext,
                            "WiFi is Blocked, Please Enable it in settings",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
            // Request android to provide the network as configured
            connectivityManager.requestNetwork(
                networkRequest,
                networkCallback, 30000
            )
        }

        fun trigger(
            network: Network,
            serverRespToken: ByteArray
        ): ByteArray {
            val socket = network.socketFactory.createSocket("10.10.10.10", 80)
            socket.setSoLinger(true, 30)
            val outputStream = socket.getOutputStream()
            outputStream.write(ByteArray(1) { 'T'.code.toByte() } + serverRespToken)
            val inputStream = socket.getInputStream()
            outputStream.flush()
            val isError = inputStream.read()
            if (isError == 1) {
                val errorLength = inputStream.read()
                val error = inputStream.readByteString(errorLength)
                throw Throwable(error.toString())
            }

            // Check for any errors
            // Read the body
            val resp = ByteArray(40)
            val readBytes = inputStream.read(resp)
            Log.d("trigger", "Read bytes: ${readBytes}")
            if (readBytes != 40) {
                throw Throwable("Invalid number of bytes read")
            }
            // clearing out the buffer
            while (inputStream.available() != 0) {
                inputStream.read()
            }
            socket.close()
            return resp
        }

        fun isUnlocked(network: Network): Boolean {
            val socket = network.socketFactory.createSocket("10.10.10.10", 80)
            socket.setSoLinger(true, 10)
            val outputStream = socket.getOutputStream()
            val inputStream = socket.getInputStream()
            outputStream.write(ByteArray(1) { 'S'.code.toByte() })
            outputStream.flush()
            val isError = inputStream.read()
            if (isError == 1) {
                val errorLength = inputStream.read()
                val error = inputStream.readByteString(errorLength)
                throw Throwable(error.toString())
            }
            val resp = inputStream.read()
            // clearing out the buffer
            while (inputStream.available() != 0) {
                inputStream.read()
            }
            socket.close()
            return resp == 1
        }
    }

}

// Used to get the Stand Locations stored in the backend
suspend fun getStandLocations(): List<StandLocation> {
    Log.d("StandLocations", "Got Some")
    return Firebase.firestore.collection("standLocations").get().await().documents.map { d ->
        Log.d("StandLocation", d.toString())
        Log.d("LocPhotoUrl", d["Photo"] as String)
        StandLocation(
            d["Location"] as String,
            d["Photo"] as String,
        )
    }.toList()
}

class StandLocation(val location: String, val photoUrl: String) {
    suspend fun getCycleNum(): Int {
        return Firebase.firestore.collection("stands").whereEqualTo("Location", location).get()
            .await().documents.map { d -> d["Cycles"] as List<*> }.sumOf { c -> c.size }
    }
}

fun isHotspotActive(context: Context): Boolean {
    val wifiManager =
        context.getSystemService(Context.WIFI_SERVICE) as WifiManager
    val method: Method = wifiManager.javaClass.getMethod(
        "getWifiApState"
    )
    method.isAccessible = true
    val invoke: Int = method.invoke(wifiManager) as Int
    return invoke == 10 || invoke == 12 || invoke == 13
}

fun isBluetoothActive(context: Context): Boolean {
    val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    return mBluetoothAdapter?.isEnabled == true
}