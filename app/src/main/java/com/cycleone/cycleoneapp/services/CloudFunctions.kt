package com.cycleone.cycleoneapp.services

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
        private const val URL = "https://cycleruncloudrun-313643300650.asia-northeast1.run.app"

        // Responsible for establishing a connection
        // Must be only called once per app instance

        suspend fun putToken(standToken: ByteArray) {
            val httpURLConnection =
                withContext(Dispatchers.IO) {
                    URI.create("$URL/update_data").toURL().openConnection() as HttpURLConnection
                }
            // Set the http request method to POST
            httpURLConnection.requestMethod = "POST"
            httpURLConnection.doOutput = true
            // Set the content type to octet stream, to be able to send binary data
            httpURLConnection.setRequestProperty("Content-Type", "application/octet-stream")
            val userToken = FirebaseAuth.getInstance().getAccessToken(true).await().token
            httpURLConnection.setRequestProperty(
                "Authorization",
                "Bearer $userToken"
            )
            // Send the Unlock Command's Data
            withContext(Dispatchers.IO) {
                httpURLConnection.outputStream.write(standToken)
                httpURLConnection.outputStream.flush()
                httpURLConnection.connect()
            }
            Log.d("PutTokenRespCode", httpURLConnection.responseCode.toString())
            var inputStream = httpURLConnection.errorStream
            if (httpURLConnection.errorStream == null) {
                inputStream = httpURLConnection.inputStream
            }
            val serverResponse = inputStream.readBytes()
            if (httpURLConnection.responseCode != 200) {
                throw Throwable("Server Error: ${serverResponse.toString(Charset.defaultCharset())}")
            }
            // Read the response
            val resp = inputStream.readBytes().toString(Charset.defaultCharset())
            Log.d("Unlock Resp", resp)
        }

        // Responsible for providing the encrypted token for unlocking the stand
        @OptIn(ExperimentalStdlibApi::class)
        suspend fun token(standToken: ByteArray): ByteArray {
            Log.d("CloudFunctions", "Sending Status")

            val httpURLConnection =
                withContext(Dispatchers.IO) {
                    (URI.create("$URL/get_token").toURL().openConnection() as HttpURLConnection)
                }
            // Set the http request method to POST
            httpURLConnection.requestMethod = "POST"
            httpURLConnection.doOutput = true
            // Set the content type to octet stream, to be able to send binary data
            httpURLConnection.setRequestProperty("Content-Type", "application/octet-stream")
            val userToken = FirebaseAuth.getInstance().getAccessToken(true).await().token
            httpURLConnection.setRequestProperty("Authorization", "Bearer $userToken")
            Log.d("Token", userToken.toString())
            // Send the Unlock Command's Data
            withContext(Dispatchers.IO) {
                httpURLConnection.outputStream.write(standToken)
                httpURLConnection.outputStream.flush()
                httpURLConnection.connect()
            }
            Log.d("Token Response Code", httpURLConnection.responseCode.toString())
            var inputStream = httpURLConnection.errorStream
            if (httpURLConnection.errorStream == null) {
                inputStream = httpURLConnection.inputStream
            }
            // Read the response
            val serverResponse = inputStream.readBytes()
            if (httpURLConnection.responseCode != 200) {
                throw Throwable("Server Error: ${serverResponse.toString(Charset.defaultCharset())}")
            }
            Log.d("ServerResp", serverResponse.toString())
            Log.d("ServerRespHex", serverResponse.toHexString(HexFormat.UpperCase))
            return serverResponse
        }
    }
}
