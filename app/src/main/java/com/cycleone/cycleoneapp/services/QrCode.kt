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

// Responsible for handling qr codes
// Simply does a callback with the data in the Qr
// Silently closes otherwise
class QrCode : Application() {

    companion object {

        var foundQrCode = false

        @OptIn(ExperimentalPermissionsApi::class)
        @Composable
        fun startCamera(onSuccess: (String) -> Unit, lifecycleOwner: LifecycleOwner) {
            // AndroidView Necessary because Compose does not allow for streaming data from camera
            // and displaying it easily
            AndroidView(factory = { context ->
                // Controls when the camera is being used
                val cameraController = LifecycleCameraController(context)
                // Options for the code scanner
                // We only care about the Qr Code, so we only have FORMAT_QR_CODE
                // Adding more will slow down scanner
                val options =
                    BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                        .build()
                val barcodeScanner = BarcodeScanning.getClient(options)
                // Specifying what happens when we get an image from camera
                cameraController.setImageAnalysisAnalyzer(
                    ContextCompat.getMainExecutor(context),
                    // Passing the image to MlKit with the barcode scanner
                    MlKitAnalyzer(
                        listOf(barcodeScanner),
                        COORDINATE_SYSTEM_VIEW_REFERENCED,
                        ContextCompat.getMainExecutor(context)
                    ) { result: MlKitAnalyzer.Result? ->
                        val barcodeResults = result?.getValue(barcodeScanner)
                        // Validating the results
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
                        // Parse to String and do the callback
                        val result = barcodeResults[0].rawValue
                        result?.let { onSuccess(it) }
                    }
                )
                // Handles the displaying of the image
                PreviewView(context).apply {
                    setBackgroundColor(0x121212ff)
                    layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                    scaleType = PreviewView.ScaleType.FILL_START
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    // Bind the camera to this view
                    post {
                        cameraController.bindToLifecycle(lifecycleOwner)
                        this.controller = cameraController
                    }
                }
            })
        }

    }

}
