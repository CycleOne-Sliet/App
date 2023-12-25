package com.cycleone.ttest2

import android.content.Context
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning


class Barcode {
    fun getMac(context: Context, cb: (ByteArray) -> Unit) {
         val options = GmsBarcodeScannerOptions.Builder().setBarcodeFormats(
             Barcode.FORMAT_QR_CODE
         ).enableAutoZoom().build()
         val scanner = GmsBarcodeScanning.getClient(context, options)

         scanner.startScan().addOnSuccessListener { cb(it.rawBytes ?: ByteArray(0)) }
     }
}
