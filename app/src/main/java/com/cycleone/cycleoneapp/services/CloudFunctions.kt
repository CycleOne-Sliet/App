package com.cycleone.cycleoneapp.services

import com.google.firebase.functions.FirebaseFunctions
import okio.ByteString.Companion.decodeBase64
import java.util.Base64

class CloudFunctions {
    companion object {
        lateinit var functions: FirebaseFunctions
        fun Connect() {
            functions = FirebaseFunctions.getInstance()
        }
        fun Token(cycle_id: ByteArray): ByteArray? {
            return Base64.getDecoder().decode(functions.getHttpsCallable("get_token").call(mapOf(Pair("cycle_id", cycle_id))).result?.data.toString())
        }
    }
}