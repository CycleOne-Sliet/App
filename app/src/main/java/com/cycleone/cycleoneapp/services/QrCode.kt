package com.cycleone.cycleoneapp.services

import android.app.Application
import android.util.Log
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.CameraController.COORDINATE_SYSTEM_VIEW_REFERENCED
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode

class QrCode : Application() {

    companion object {

        var foundQrCode = false

        @OptIn(ExperimentalPermissionsApi::class)
        @Composable
        fun startCamera(onSuccess: (String) -> Unit, lifecycleOwner: LifecycleOwner) {

            AndroidView(factory = { context ->

                val cameraController = LifecycleCameraController(context)

                val options =
                    BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                        .build()
                val barcodeScanner = BarcodeScanning.getClient(options)

                cameraController.setImageAnalysisAnalyzer(
                    ContextCompat.getMainExecutor(context),
                    MlKitAnalyzer(
                        listOf(barcodeScanner),
                        COORDINATE_SYSTEM_VIEW_REFERENCED,
                        ContextCompat.getMainExecutor(context)
                    ) { result: MlKitAnalyzer.Result? ->
                        val barcodeResults = result?.getValue(barcodeScanner)
                        if (barcodeResults != null) {
                            Log.i("QR Codes", barcodeResults.toList().toString())
                        } else {
                            Log.i("QR Code", "No results")
                        }
                        if ((barcodeResults == null) ||
                            (barcodeResults.size == 0) ||
                            (barcodeResults.first() == null)
                        ) {
                            return@MlKitAnalyzer
                        }
                        val result = barcodeResults[0].rawValue
                        result?.let { onSuccess(it) }
                    }
                )
                PreviewView(context).apply {
                    setBackgroundColor(0x121212ff)
                    layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                    scaleType = PreviewView.ScaleType.FILL_START
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    post {
                        cameraController.bindToLifecycle(lifecycleOwner)
                        this.controller = cameraController
                    }
                }
            })
        }

    }

}
