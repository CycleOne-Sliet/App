package com.cycleone.cycleoneapp.services

import android.app.Application
import android.content.pm.PackageManager.FEATURE_WIFI_AWARE
import android.net.wifi.aware.AttachCallback
import android.net.wifi.aware.DiscoverySessionCallback
import android.net.wifi.aware.PeerHandle
import android.net.wifi.aware.SubscribeConfig
import android.net.wifi.aware.SubscribeDiscoverySession
import android.net.wifi.aware.WifiAwareManager
import android.net.wifi.aware.WifiAwareSession
import com.cycleone.cycleoneapp.services.QrCode.Companion.appContext
import com.daveanthonythomas.moshipack.MoshiPack
import com.google.firebase.auth.FirebaseUser
import java.util.UUID

sealed class Command {
    data class Unlock(val user:String, val serverRespToken: ByteArray) : Command()
    class GetStatus : Command()
}

public data class Response(val id: ByteArray,val  isUnlocked: Boolean, val cycleId: ByteArray)

class Stand(
    onUnavailable: () -> Unit,
    onAttach: (WifiAwareSession) -> Unit,
    onAttachFailure: () -> Unit
) : Application() {
    override fun onCreate() {
        super.onCreate()
        QrCode.Companion.appContext = applicationContext
    }

    init {
        if (!packageManager.hasSystemFeature(FEATURE_WIFI_AWARE)) {
            onUnavailable()
        } else {
            Companion.wifiAwareManager = getSystemService(WifiAwareManager::class.java)
            wifiAwareManager.attach(object : AttachCallback() {
                override fun onAttached(session: WifiAwareSession?) {
                    super.onAttached(session)
                    if (session != null) {
                        Companion.wifiAwareSession = session
                        onAttach(session)
                    }
                }

                override fun onAttachFailed() {
                    super.onAttachFailed()
                    onAttachFailure()
                }
            }, null)
            Companion.moshiPack = MoshiPack()
        }
    }




    companion object {
        lateinit var wifiAwareSession: WifiAwareSession
        lateinit var wifiAwareManager: WifiAwareManager
        lateinit var discoverySession: SubscribeDiscoverySession
        lateinit var config: SubscribeConfig
        lateinit var peerHandle: PeerHandle
        lateinit var moshiPack: MoshiPack

        fun GetStatus() {
            val payload = moshiPack.pack(Command.GetStatus()).readByteArray()
            discoverySession.sendMessage(peerHandle, UUID.randomUUID().hashCode(), payload)
        }

        fun Connect(ident: ByteArray, onResponse: (Response) -> Unit) {
            config = SubscribeConfig.Builder().setServiceName("CycleOneStand").setServiceSpecificInfo(ident).build()
            wifiAwareSession.subscribe(config, object : DiscoverySessionCallback() {
                override fun onSubscribeStarted(session: SubscribeDiscoverySession) {
                    super.onSubscribeStarted(session)
                    discoverySession = session
                }

                override fun onMessageReceived(peerHandle: PeerHandle?, message: ByteArray?) {
                    super.onMessageReceived(peerHandle, message)
                    message?.let { onResponse(moshiPack.unpack<Response>(it)) }
                }
            }, null)
        }

        fun Unlock(uid:String, serverRespToken: ByteArray) {
            discoverySession.updateSubscribe(config)
            val payload = moshiPack.pack(Command.Unlock(uid, serverRespToken)).readByteArray()
            discoverySession.sendMessage(peerHandle, UUID.randomUUID().hashCode(), payload)
        }
    }
}