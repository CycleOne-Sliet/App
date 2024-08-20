package com.cycleone.cycleoneapp.services

import android.util.Base64
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URI
import java.nio.charset.Charset

// Class used to communicate with the backend
class CloudFunctions {
    companion object {
        // lateinit var functions: FirebaseFunctions
        val url = "https://cycleruncloudrun-bssblvm4cq-an.a.run.app"

        // Responsible for establishing a connection
        // Must be only called once per app instance
        fun Connect() {
            //functions = FirebaseFunctions.getInstance()
        }

        suspend fun PutToken(standToken: ByteArray) {
            Log.d("CloudFunctions", "Sending Status")
            val token = Base64.encodeToString(standToken, Base64.NO_WRAP)
            Log.d("PutToken", token)

            var httpURLConnection =
                withContext(Dispatchers.IO) {
                    URI.create("$url/update_data").toURL().openConnection() as HttpURLConnection
                }
            // Set the http request method to POST
            httpURLConnection.requestMethod = "POST"
            httpURLConnection.doOutput = true
            // Set the content type to octet stream, to be able to send binary data
            httpURLConnection.setRequestProperty("Content-Type", "text/plain")
            val userToken = FirebaseAuth.getInstance().getAccessToken(true).await().token
            httpURLConnection.setRequestProperty(
                "Authorization",
                "Bearer ${userToken}"
            )
            // Send the Unlock Command's Data
            withContext(Dispatchers.IO) {
                httpURLConnection.outputStream.write(token.toByteArray())
                httpURLConnection.outputStream.flush()
                httpURLConnection.connect()
            }
            var inputStream = httpURLConnection.inputStream
            // Read the response
            val resp = inputStream.readBytes().toString(Charset.defaultCharset())
            Log.d("Unlock Resp", resp)
        }

        // Responsible for providing the encrypted token for unlocking the stand
        @OptIn(ExperimentalStdlibApi::class)
        suspend fun Token(standToken: ByteArray): ByteArray {
            Log.d("CloudFunctions", "Sending Status")
            val token = Base64.encodeToString(standToken, Base64.NO_WRAP)
            Log.d("PutToken", token)

            var httpURLConnection =
                withContext(Dispatchers.IO) {
                    (URI.create("$url/get_token").toURL().openConnection() as HttpURLConnection)
                }
            // Set the http request method to POST
            httpURLConnection.requestMethod = "POST"
            httpURLConnection.doOutput = true
            // Set the content type to octet stream, to be able to send binary data
            httpURLConnection.setRequestProperty("Content-Type", "text/plain")
            val userToken = FirebaseAuth.getInstance().getAccessToken(true).await().token
            httpURLConnection.setRequestProperty("Authorization", "Bearer $userToken")
            Log.d("Token", userToken.toString())
            // Send the Unlock Command's Data
            withContext(Dispatchers.IO) {
                Log.d("StandToken", token)
                httpURLConnection.outputStream.write(token.toByteArray())
                httpURLConnection.outputStream.flush()
                httpURLConnection.connect()
            }
            Log.d("Token Response Code", httpURLConnection.responseCode.toString())
            var inputStream = httpURLConnection.inputStream
            // Read the response
            val serverResponse = inputStream.readBytes()
            Log.d("ServerResp", serverResponse.toString())
            Log.d("ServerRespHex", serverResponse.toHexString(HexFormat.UpperCase))
            return inputStream.readBytes()
        }
    }
}