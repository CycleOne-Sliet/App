package com.cycleone.cycleoneapp.services

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
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
import java.net.InetSocketAddress
import java.net.Socket
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
            is Unlock -> return user.toByteArray().size + serverRespToken.size + 3
            is GetStatus -> return 1
        }
    }

    fun getData(): ByteArray {
        var bytes = ByteArray(this.getEncodedSize())
        bytes[0] = this.getId()
        when (this) {
            is Unlock -> {
                val userBytes = user.toByteArray()
                bytes[1] = userBytes.size.toByte()
                userBytes.copyInto(bytes, 2)
                bytes[userBytes.size + 2] = serverRespToken.size.toByte()
                serverRespToken.copyInto(bytes, userBytes.size + 3)
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

        val parser = Moshi.Builder().add(ResponseAdapter())
            .add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory()).build()
            .adapter<Response>()

        fun GetStatus(socket: Socket): Response? {

            if (!socket.isConnected || socket.isClosed) {
                socket.connect(InetSocketAddress("10.10.10.10", 80))
            }
            socket.getOutputStream().write(Command.GetStatus().getData())
            socket.getOutputStream().flush()
            val inputStream = socket.getInputStream()
            val respLen = inputStream.read()
            val respBytes = ByteArray(respLen)
            inputStream.read(respBytes)
            while (inputStream.available() > 0) {
                inputStream.read()
            }
            val resp = respBytes.toString(Charset.defaultCharset())

            Log.d("StatusResp", resp)
            try {

                val parsedResp = parser.fromJson(resp)
                return parsedResp
            } catch (err: Throwable) {

                Log.d("Parsing Resp", "Message: ${err.message}\nStacktrace: ${err.stackTrace}")
                return null
            }
        }

        fun Connect(mac: MacAddress, onConnect: (socket: Socket) -> Unit) {
            Log.d("MacAddress used: ", mac.toString())
            val wifiNetworkSpecifier =
                WifiNetworkSpecifier.Builder().setWpa2Passphrase("CycleOne").setIsHiddenSsid(true)
                    .setSsid("CycleOneS1").build()

            val networkRequest = NetworkRequest.Builder().addTransportType(TRANSPORT_WIFI)
                .setNetworkSpecifier(wifiNetworkSpecifier).removeCapability(
                    NET_CAPABILITY_INTERNET
                ).build()
            val connectivityManager =
                appContext.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            connectivityManager.requestNetwork(
                networkRequest,
                object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        super.onAvailable(network)
                        Log.d("ConnManager", "Connected")
                        val socket = network.socketFactory.createSocket()
                        onConnect(socket)
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

                })

        }

        fun Unlock(socket: Socket, uid: String, serverRespToken: ByteArray): Response? {

            if (!socket.isConnected || socket.isClosed) {
                socket.connect(InetSocketAddress("10.10.10.10", 80))
            }
            val data = Command.Unlock(uid, serverRespToken).getData()

            Log.i("Data sent", data.toHexString())
            val outputStream = socket.getOutputStream()
            outputStream.write(data)
            outputStream.flush()

            val inputStream = socket.getInputStream()
            val respLen = inputStream.read()
            val respBytes = ByteArray(respLen)
            inputStream.read(respBytes)
            while (inputStream.available() > 0) {
                inputStream.read()
            }
            val resp = respBytes.toString(Charset.defaultCharset())
            try {
                return parser.fromJson(resp)
            } catch (err: Throwable) {
                Log.e("Parsing response", "${err.message}\n${err.stackTrace.asList()}")
                return null
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