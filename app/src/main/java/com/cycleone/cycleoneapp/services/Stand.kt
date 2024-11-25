package com.cycleone.cycleoneapp.services

import android.app.Activity
import android.app.Application
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
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.squareup.moshi.FromJson
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.adapter
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URI


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
            val httpURLConnection =
                network.openConnection(
                    URI.create("http://10.10.10.10/").toURL()
                ) as HttpURLConnection
            httpURLConnection.requestMethod = "GET"
            httpURLConnection.connect()
            // Check for any errors
            var inputStream = httpURLConnection.errorStream
            if (inputStream == null) {
                inputStream = httpURLConnection.inputStream
            }
            // Read the body
            return inputStream.readBytes()
        }


        // Connects to the stand over the mac address
        suspend fun connect(
            mac: MacAddress,
            context: Context,
            onError: (String) -> Unit,
            onConnect: suspend (Network) -> Unit
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
                    Log.e("Unavailable", "Network is not found")
                    Toast.makeText(
                        appContext,
                        "Stand is not available, Check if a stand is near",
                        Toast.LENGTH_LONG
                    ).show()
                    onError("Stand Unavailable")
                }


                // Called when the Wifi is Blocked, We send the user to Settings to unblock it
                override fun onBlockedStatusChanged(network: Network, blocked: Boolean) {
                    super.onBlockedStatusChanged(network, blocked)
                    if (blocked) {
                        onError("Blocked")
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

        suspend fun trigger(
            network: Network,
            serverRespToken: ByteArray
        ): ByteArray {
            NavProvider.addLogEntry(
                "Sending trigger command",
            )
            // Establish the connection
            val httpURLConnection =
                network.openConnection(
                    URI.create("http://10.10.10.10").toURL()
                ) as HttpURLConnection
            // Set the http request method to POST
            httpURLConnection.doOutput = true
            httpURLConnection.requestMethod = "POST"
            httpURLConnection.connectTimeout = 11000
            httpURLConnection.readTimeout = 11000
            // Set the content type to octet stream, to be able to send binary data
            httpURLConnection.setRequestProperty("Content-Type", "application/octet-stream")
            // Send the Unlock Command's Data
            Log.d("ServerRespLen", serverRespToken.size.toString())
            httpURLConnection.outputStream.write(serverRespToken)
            httpURLConnection.outputStream.flush()
            // Perform the request
            httpURLConnection.connect()
            NavProvider.addLogEntry(
                "Trigger Command sent",
            )
            // Check for any errors
            var inputStream = httpURLConnection.errorStream
            if (inputStream == null) {
                inputStream = httpURLConnection.inputStream
            }
            Log.d("Unlock RespCode", "${httpURLConnection.responseCode}")
            Log.d("Unlock RespMsg", httpURLConnection.responseMessage)
            NavProvider.addLogEntry(
                "Stand Response code: ${httpURLConnection.responseCode}",
            )
            // Read the response
            return inputStream.readBytes()
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
