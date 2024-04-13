package com.cycleone.cycleoneapp.services

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
import android.net.wifi.WifiNetworkSpecifier
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.squareup.moshi.FromJson
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.adapter
import kotlinx.coroutines.tasks.await
import java.net.HttpURLConnection
import java.net.URI
import java.nio.charset.Charset


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
        when (this) {
            is Unlock -> return user.toByteArray().size + serverRespToken.size + 2
            is GetStatus -> return 0
        }
    }

    // Returns the byte representation of the command
    // Suitable to be sent to the stand
    fun getData(): ByteArray {
        var bytes = ByteArray(this.getEncodedSize())
        when (this) {
            is Unlock -> {
                val userBytes = user.toByteArray()
                // Put the length of user id as the first byte
                bytes[0] = userBytes.size.toByte()
                // Put the data of the user id after it
                userBytes.copyInto(bytes, 1)
                // Put the length of the server token after that
                bytes[userBytes.size + 1] = serverRespToken.size.toByte()
                // Put the data of the server token after that
                serverRespToken.copyInto(bytes, userBytes.size + 2)
                return bytes
            }

            is GetStatus -> {
            }
        }
        return bytes
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

        val parser = Moshi.Builder().add(ResponseAdapter())
            .add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory()).build()
            .adapter<Response>()

        // Disconnecting from the stand
        // Should be called as soon as the exchange between stand and mobile has occurred
        // Frees up the wifi
        fun Disconnect() {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }

        // Get the stand's status
        // Must be called after Connect has been called
        fun GetStatus(network: Network): Response? {
            // Establish an Http Connection
            var httpURLConnection =
                network.openConnection(
                    URI.create("http://10.10.10.10/").toURL()
                ) as HttpURLConnection
            httpURLConnection.connect()
            // Check for any errors
            var inputStream = httpURLConnection.errorStream
            if (inputStream == null) {
                inputStream = httpURLConnection.inputStream
            }
            // Read the body
            val resp = inputStream.readBytes().toString(Charset.defaultCharset())

            Log.d("StatusResp", resp)
            return try {
                // Try parsing the body into StandResponse
                parser.fromJson(resp)
            } catch (err: Throwable) {
                // Something unrecognizable was encountered
                Log.d("Parsing Resp", "Message: ${err.message}\nStacktrace: ${err.stackTrace}")
                null
            }
        }

        // Connects to the stand over the mac address

        fun Connect(mac: MacAddress, onConnect: (Network) -> Unit) {
            // Used to configure the wifi network
            // We are setting the Ssid to CycleOneS1
            // Password to CycleOne and mac address to whatever that was passed in
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
            connectivityManager =
                appContext.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            // Callbacks for when network state changes
            networkCallback = object : NetworkCallback() {
                // When network becomes available, call the onConnect function, and then disconnect
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    onConnect(network)
                    Disconnect()
                }

                // Called when the Wifi is Blocked, We send the user to Settings to unblock it
                override fun onBlockedStatusChanged(network: Network, blocked: Boolean) {
                    super.onBlockedStatusChanged(network, blocked)
                    if (blocked)
                        Log.e("Unavailable", "Network is Blocked")
                    Toast.makeText(
                        appContext,
                        "WiFi is Blocked, Please Enable it in settings",
                        Toast.LENGTH_LONG
                    ).show()
                    startActivity(appContext, Intent(Settings.ACTION_WIFI_SETTINGS), null)
                }

                // When the Network is Unavailable, show the toast and send the user to setting
                // NOTE: Sometimes this triggers, even when the network may be available
                override fun onUnavailable() {
                    super.onUnavailable()
                    Log.e("Unavailable", "Network is unavailable")
                    Toast.makeText(
                        appContext,
                        "Turn on WiFi, or stand is offline",
                        Toast.LENGTH_LONG
                    ).show()
                    startActivity(appContext, Intent(Settings.ACTION_WIFI_SETTINGS), null)
                }
            }


            // Request android to provide the network as configured
            connectivityManager.requestNetwork(
                networkRequest,
                networkCallback
            )
        }

        // Used for sending the unlock request to the stand
        fun Unlock(network: Network, uid: String, serverRespToken: ByteArray): Response? {

            // Establish the connection
            var httpURLConnection =
                network.openConnection(
                    URI.create("http://10.10.10.10/").toURL()
                ) as HttpURLConnection
            // Set the http request method to POST
            httpURLConnection.doOutput = true
            httpURLConnection.requestMethod = "POST"
            // Set the content type to octet stream, to be able to send binary data
            httpURLConnection.setRequestProperty("Content-Type", "application/octet-stream")
            // Send the Unlock Command's Data
            httpURLConnection.outputStream.write(Command.Unlock(uid, serverRespToken).getData())
            httpURLConnection.outputStream.flush()
            // Perform the request
            httpURLConnection.connect()
            // Check for any errors
            var inputStream = httpURLConnection.errorStream
            if (inputStream == null) {
                inputStream = httpURLConnection.inputStream
            }
            Log.d("Unlock RespCode", "${httpURLConnection.responseCode}")
            Log.d("Unlock RespMsg", httpURLConnection.responseMessage)
            // Read the response
            val resp = inputStream.readBytes().toString(Charset.defaultCharset())
            Log.d("Unlock Resp", resp)
            return try {
                // Parse it to StandResponse
                parser.fromJson(resp)
            } catch (err: Throwable) {
                Log.e("Parsing response", "${err.message}\n${err.stackTrace.asList()}")
                null
            }
        }
    }

}

// Used to get the Stand Locations stored in the backend
suspend fun getStandLocations(): List<StandLocation> {
    return Firebase.firestore.collection("stands").get().await().documents.map { d ->
        Log.d("LocPhotoUrl", d["photo"] as String)
        StandLocation(
            d["location"] as String,
            d["photo"] as String,
            d["cycles"]?.let { cycleList -> return@let (cycleList as List<*>).map { cycle -> cycle as String } }!!
        )
    }.toList()
}

data class StandLocation(val location: String, val photoUrl: String, val cycles: List<String>)
