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
import android.net.wifi.aware.WifiAwareSession
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


sealed class Command {
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

    class GetStatus : Command()

    fun getId(): Byte {
        when (this) {
            is Unlock -> {
                return 'U'.code.toByte()
            }

            is GetStatus -> {
                return 'G'.code.toByte()
            }
        }
    }

    fun getEncodedSize(): Int {
        when (this) {
            is Unlock -> return user.toByteArray().size + serverRespToken.size + 2
            is GetStatus -> return 1
        }
    }

    fun getData(): ByteArray {
        var bytes = ByteArray(this.getEncodedSize())
        when (this) {
            is Unlock -> {
                val userBytes = user.toByteArray()
                bytes[0] = userBytes.size.toByte()
                userBytes.copyInto(bytes, 1)
                bytes[userBytes.size + 1] = serverRespToken.size.toByte()
                serverRespToken.copyInto(bytes, userBytes.size + 2)
                return bytes
            }

            is GetStatus -> {
            }
        }
        return bytes
    }
}

sealed class Response {
    data class Ok(val isUnlocked: Boolean, val cycleId: String?) : Response()
    data class Err(val error: String) : Response()
}

data class ResponseJson(val isUnlocked: Boolean?, val cycleId: String?, val error: String?)

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
class Stand(
    onUnavailable: () -> Unit,
    onAttach: (WifiAwareSession) -> Unit,
    onAttachFailure: () -> Unit
) : Application() {
    companion object {
        lateinit var appContext: Context
        lateinit var connectivityManager: ConnectivityManager
        lateinit var networkCallback: NetworkCallback

        val parser = Moshi.Builder().add(ResponseAdapter())
            .add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory()).build()
            .adapter<Response>()

        fun Disconnect() {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }

        fun GetStatus(network: Network): Response? {


            var httpURLConnection =
                network.openConnection(
                    URI.create("http://10.10.10.10/").toURL()
                ) as HttpURLConnection
            httpURLConnection.connect()
            var inputStream = httpURLConnection.errorStream
            if (inputStream == null) {
                inputStream = httpURLConnection.inputStream
            }
            val resp = inputStream.readBytes().toString(Charset.defaultCharset())

            Log.d("StatusResp", resp)
            return try {
                parser.fromJson(resp)
            } catch (err: Throwable) {

                Log.d("Parsing Resp", "Message: ${err.message}\nStacktrace: ${err.stackTrace}")
                null
            }
        }

        fun Connect(mac: MacAddress, onConnect: (Network) -> Unit) {
            Log.d("MacAddress used: ", mac.toString())
            val wifiNetworkSpecifier =
                WifiNetworkSpecifier.Builder().setWpa2Passphrase("CycleOne").setIsHiddenSsid(true)
                    .setBssid(mac)
                    .build()

            val networkRequest = NetworkRequest.Builder().addTransportType(TRANSPORT_WIFI)
                .setNetworkSpecifier(wifiNetworkSpecifier).removeCapability(
                    NET_CAPABILITY_INTERNET
                ).build()
            connectivityManager =
                appContext.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            networkCallback = object : NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    Log.d("ConnManager", "Connected")
                    onConnect(network)
                }

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
            connectivityManager.requestNetwork(
                networkRequest,
                networkCallback
            )

        }

        fun Unlock(network: Network, uid: String, serverRespToken: ByteArray): Response? {

            Log.d("Stand", "Unlocking")
            var httpURLConnection =
                network.openConnection(
                    URI.create("http://10.10.10.10/").toURL()
                ) as HttpURLConnection
            httpURLConnection.doOutput = true
            httpURLConnection.requestMethod = "POST"
            httpURLConnection.setRequestProperty("Content-Type", "application/octet-stream")
            httpURLConnection.outputStream.write(Command.Unlock(uid, serverRespToken).getData())
            httpURLConnection.outputStream.flush()
            httpURLConnection.connect()
            var inputStream = httpURLConnection.errorStream
            if (inputStream == null) {
                inputStream = httpURLConnection.inputStream
            }
            Log.d("Unlock RespCode", "${httpURLConnection.responseCode}")
            Log.d("Unlock RespMsg", httpURLConnection.responseMessage)
            val resp = inputStream.readBytes().toString(Charset.defaultCharset())
            Log.d("Unlock Resp", resp)
            return try {
                parser.fromJson(resp)
            } catch (err: Throwable) {
                Log.e("Parsing response", "${err.message}\n${err.stackTrace.asList()}")
                null
            }
        }
    }

}

suspend fun getStandLocations(): List<StandLocation> {
    return Firebase.firestore.collection("stands").get().await().documents.map { d ->
        StandLocation(
            d["location"] as String,
            d["photo"] as String
        )
    }.toList()
}

data class StandLocation(val location: String, val photoUrl: String)
