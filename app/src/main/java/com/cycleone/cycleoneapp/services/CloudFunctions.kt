package com.cycleone.cycleoneapp.services

import android.net.MacAddress
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URI
import java.nio.charset.Charset
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.Cipher.DECRYPT_MODE
import javax.crypto.Cipher.ENCRYPT_MODE
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.experimental.and
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

// Class used to communicate with the backend
class CloudFunctions {
    companion object {
        private const val URL = "https://cycleruncloudrun-313643300650.asia-northeast1.run.app"

        @OptIn(ExperimentalEncodingApi::class)
        private val KEY = MessageDigest.getInstance("SHA256")
            .digest(Base64.Default.decode("HhcTZuPt3OKSO8doYjYG8Q=="))


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

        fun token(standToken: ByteArray): ByteArray {
            val unencryptedResp = ByteArray(16)
            for (i in 0..<8) {
                unencryptedResp[i] = (Math.random() * 256).toInt().toByte()
            }
            val standState = decodeResp(standToken)
            if (standState.second) {
                unencryptedResp[8] = 0
            } else {
                unencryptedResp[8] = 1
            }
            val response = ByteArray(40)
            for (i in 0..<8) {
                response[i] = unencryptedResp[i]
            }
            val iv = ByteArray(16)
            for (i in 0..16) {
                iv[i] = (Math.random() * 256).toInt().toByte()
                response[8 + i] = iv[i]
            }
            val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
            cipher.init(ENCRYPT_MODE, SecretKeySpec(KEY, "AES"), IvParameterSpec(iv))
            cipher.update(unencryptedResp)
            cipher.doFinal(response, 24)
            return response

        }

        private fun decodeResp(data: ByteArray): Pair<MacAddress, Boolean> {
            if (data.size != 40) {
                throw Throwable("Data length is not equal to 40")
            }
            val data = data.copyOfRange(8, 40)
            val iv = data.copyOfRange(0, 16)
            val cipherBytes = data.copyOfRange(16, 32)
            val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
            cipher.init(DECRYPT_MODE, SecretKeySpec(KEY, "AES"), IvParameterSpec(iv))
            val cipherText = cipher.doFinal(cipherBytes)
            val isStandUnlocked = cipherText[8] and 0x01
            val macAddressBytes = cipherText.copyOfRange(9, 15)
            val macAddress = MacAddress.fromBytes(macAddressBytes)
            return Pair(macAddress, isStandUnlocked == 0x01.toByte())
        }
        /*

func decodeResp(data []byte) (net.HardwareAddr, bool, bool, error) {
	if len(data) != 40 {
		return nil, false, false, errors.New("Data length is not equal to 40")
	}
	decryptedResp := [16]byte{}
	decryptor := cipher.NewCBCDecrypter(keyCipher, data[8:24])
	decryptor.CryptBlocks(decryptedResp[:], data[24:])
	valid := true
	for i := 0; i < 8; i++ {
		if decryptedResp[i] != data[i] {
			valid = false
			break
		}
	}
	if !valid {
		return nil, false, false, errors.New("Verification id mismatch")
	}
	isStandUnlocked := (decryptedResp[8] & 0x01) == 1
	strike := decryptedResp[8] > 1

	macAddressBytes := decryptedResp[9:15]
	macAddress := net.HardwareAddr(macAddressBytes)
	return macAddress, isStandUnlocked, strike, nil
}
         */
        // Responsible for providing the encrypted token for unlocking the stand
        /*
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
        }*/
    }
}
