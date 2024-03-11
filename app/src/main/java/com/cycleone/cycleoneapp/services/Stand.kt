package com.cycleone.cycleoneapp.services

import android.app.Application
import android.content.pm.PackageManager.FEATURE_WIFI_AWARE
import android.net.ConnectivityManager
import android.net.MacAddress
import android.net.Network
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.net.NetworkRequest
import android.net.NetworkSpecifier
import android.net.wifi.WifiNetworkSpecifier
import android.net.wifi.aware.AttachCallback
import android.net.wifi.aware.DiscoverySessionCallback
import android.net.wifi.aware.PeerHandle
import android.net.wifi.aware.SubscribeConfig
import android.net.wifi.aware.SubscribeDiscoverySession
import android.net.wifi.aware.WifiAwareManager
import android.net.wifi.aware.WifiAwareSession
import androidx.core.content.ContextCompat.getSystemService
import com.cycleone.cycleoneapp.services.QrCode.Companion.appContext
import com.daveanthonythomas.moshipack.MoshiPack
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.remote.FirestoreChannel
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.adapter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketAddress
import java.util.UUID

sealed class Command {
    data class Unlock(val user:String, val serverRespToken: ByteArray) : Command();
    class GetStatus() : Command();
    fun getId(): Byte {
        when (this) {
            is Command.Unlock -> {return 0}
            is Command.GetStatus -> {return 1}
        }
    }
    fun getEncodedSize(): Int {
        when (this) {
            is Command.Unlock -> return user.toByteArray().size + serverRespToken.size + 3
            is Command.GetStatus -> return 1
        }
    }
    fun getData(): ByteArray {
        var bytes = ByteArray(this.getEncodedSize())
        bytes[0] = this.getId()
        when (this) {
            is Command.Unlock -> {
                val userBytes = user.toByteArray()
                bytes[1] = userBytes.size.toByte()
                userBytes.copyInto(bytes, 2)
                bytes[userBytes.size + 2] = serverRespToken.size.toByte()
                serverRespToken.copyInto(bytes, userBytes.size + 3)
                return bytes
            }
            is Command.GetStatus -> {
            }
        }
        return bytes
    }
}

sealed class  Response {
    data class Ok(val  isUnlocked: Boolean, val cycleId: ByteArray?) : Response()
    data class Err(val msg: String) : Response()
}

data class ResponseJson(val isUnlocked: Boolean?, val cycleId: ByteArray?, val error: String?)

class ResponseAdapter {
    @ToJson fun toJson(resp: Response): ResponseJson{
        return when (resp) {
            is Response.Ok -> ResponseJson(resp.isUnlocked, resp.cycleId, null)
            is Response.Err -> ResponseJson(null, null, resp.msg)
        }
    }
    @FromJson fun fromJson( response: ResponseJson): Response {
        return if (response.isUnlocked != null && response.cycleId != null) {
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

        lateinit var socket: Socket
        val parser = Moshi.Builder().add(ResponseAdapter()).add(KotlinJsonAdapterFactory()).build().adapter<Response>()
        fun GetStatus() : Response? {
            socket.getOutputStream().write(Command.GetStatus().getData())
            val resp = socket.getInputStream().readBytes().toString()
            return parser.fromJson(resp)
        }

        fun Connect(mac: MacAddress) {
            val wifiNetworkSpecifier = WifiNetworkSpecifier.Builder().setBssid(mac).setWpa2Passphrase("CycleOne").setIsHiddenSsid(true).build()

            val networkRequest = NetworkRequest.Builder().setNetworkSpecifier(wifiNetworkSpecifier).addTransportType(TRANSPORT_WIFI).removeCapability(
                NET_CAPABILITY_INTERNET).build()
            val connectivityManager = appContext.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            connectivityManager.requestNetwork(networkRequest, object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    socket = Socket()
                    network.bindSocket(socket)
                    socket.connect(InetSocketAddress("10.10.10.10", 80))
                }

                override fun onUnavailable() {
                    super.onUnavailable()
                }
            })
        }

        fun Unlock(uid:String, serverRespToken: ByteArray): Response? {
            socket.getOutputStream().write(Command.Unlock(uid, serverRespToken).getData())
            val resp = socket.getInputStream().readBytes().toString()
            return parser.fromJson(resp)
        }
    }
}

suspend fun getStandLocations() : List<StandLocation> {
      return Firebase.firestore.collection("stands").get().await().documents.map { d -> StandLocation(d["location"] as String, d["photo"] as String) }.toList()
}
data class StandLocation(val location: String, val photoUrl: String)