package com.cycleone.cycleoneapp.services

import android.util.Log
import com.cycleone.cycleoneapp.ui.screens.decodeHexFromStr
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.tasks.await
import okio.ByteString.Companion.decodeBase64
import okio.ByteString.Companion.decodeHex
import java.util.Base64

class CloudFunctions {
    companion object {
        lateinit var functions: FirebaseFunctions
        fun Connect() {
            functions = FirebaseFunctions.getInstance()
        }
        @OptIn(ExperimentalStdlibApi::class)
        suspend fun Token(cycle_id: String): ByteArray? {
            try {
                val request = hashMapOf(
                    "cycle_id" to cycle_id
                )
                Log.d("Token CycleId", cycle_id)
                Log.d("CycleId in hashmap", request["cycle_id"] as String)
                val result = functions.getHttpsCallable("get_token").call(request)
                    .await()
                val data = result.data as Map<*, *>
                val token = (data["token"] as String).removePrefix("b'").removeSuffix("'")
                return decodeHexFromStr(token)
            } catch (err: Throwable) {
                Log.e("Functions Fked", "${err.message} ${err.stackTrace} $err")
                return null
            }
        }
    }
}