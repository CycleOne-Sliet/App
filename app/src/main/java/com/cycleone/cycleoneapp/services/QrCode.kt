package com.cycleone.cycleoneapp.services

import android.app.Application
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

class QrCode : Application() {
    override fun onCreate() {
        super.onCreate()
        QrCode.appContext = applicationContext
    }

    companion object {

            fun create(onSuccess: (ByteArray) -> Unit) {
                val options = GmsBarcodeScannerOptions.Builder()
                    .setBarcodeFormats(
                        Barcode.FORMAT_QR_CODE)
                    .enableAutoZoom()
                    .build()
                val scanner = GmsBarcodeScanning.getClient(appContext, options).startScan().addOnSuccessListener { barcode -> println(barcode)
                    barcode.rawBytes?.let { onSuccess(it) }
                }.addOnCanceledListener { println("Canceled") }.addOnFailureListener{e -> println(e)}


        }

        lateinit var appContext: Context
    }

}