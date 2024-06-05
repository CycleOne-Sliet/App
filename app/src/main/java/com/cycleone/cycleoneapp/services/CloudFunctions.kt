package com.cycleone.cycleoneapp.services

import android.util.Base64
import android.util.Log
import com.cycleone.cycleoneapp.ui.screens.decodeHexFromStr
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.tasks.await
import java.nio.charset.Charset

// Class used to communicate with the backend
class CloudFunctions {
    companion object {
        lateinit var functions: FirebaseFunctions

        // Responsible for establishing a connection
        // Must be only called once per app instance
        fun Connect() {
            functions = FirebaseFunctions.getInstance()
        }

        suspend fun PutToken(standToken: ByteArray) {
            Log.d("CloudFunctions", "Sending Status")
            val request = hashMapOf(
                "token" to Base64.encodeToString(standToken, Base64.NO_WRAP)
            )
            // Call the function get_token with the cycle_id as an argument
            val result = functions.getHttpsCallable("update_data").call(request)
                .await()
            // Get the token, strip `b'` from the front and `'` from the end
            val data = result.data as Map<*, *>
            val token = (data["token"] as String).removePrefix("b'").removeSuffix("'")
            // Parse the hex into binary and return
        }

        // Responsible for providing the encrypted token for unlocking the stand
        @OptIn(ExperimentalStdlibApi::class)
        suspend fun Token(standToken: ByteArray): ByteArray? {
            Log.d("Token", standToken.toString(Charset.defaultCharset()))
            Log.d("CloudFunctions", "Unlocking")
            try {
                val request = hashMapOf(
                    "token" to Base64.encodeToString(standToken, Base64.NO_WRAP)
                )
                // Call the function get_token with the cycle_id as an argument
                val result = functions.getHttpsCallable("get_token").call(request)
                    .await()
                // Get the token, strip `b'` from the front and `'` from the end
                val data = result.data as Map<*, *>
                val token = (data["token"] as String).removePrefix("b'").removeSuffix("'")
                // Parse the hex into binary and return
                return decodeHexFromStr(token)
            } catch (err: Throwable) {
                // Should not happen, ever
                // Basically means that the function on firebase has crashed
                // Someone has to fix it in the CycleOneFunctions repo
                Log.e("Functions Fked", "${err.message} ${err.stackTrace} $err")
                return null
            }
        }
    }
}