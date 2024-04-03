package com.cycleone.cycleoneapp.services

import android.app.Application
import android.util.Log
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import android.widget.Toast
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.CameraController.COORDINATE_SYSTEM_VIEW_REFERENCED
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode

class QrCode : Application() {

    companion object {

        var foundQrCode = false

        @OptIn(ExperimentalPermissionsApi::class)
        @Composable
        fun startCamera(onSuccess: (String) -> Unit, lifecycleOwner: LifecycleOwner) {

            val wifiPermissionState = rememberPermissionState(
                android.Manifest.permission.CHANGE_WIFI_STATE
            )

            val cameraPermissionState = rememberPermissionState(
                android.Manifest.permission.CAMERA
            )

            if (cameraPermissionState.status.isGranted) {
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
                            if (wifiPermissionState.status.isGranted) {
                                result?.let { onSuccess(it) }
                            } else {
                                if (wifiPermissionState.status.shouldShowRationale) {
                                    Toast.makeText(
                                        context,
                                        "Wifi Permission is required to connect to the stand",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Wifi Permission is required to connect to the stand\nWithout it, this will not work",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
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
            } else {
                if (cameraPermissionState.status.shouldShowRationale) {
                    Text("Camera Permission is needed to scan the qr code of the scanner")
                } else {
                    Text("Camera Permission is not Granted, Without it, We cannot scan QR")
                }
            }
        }

    }

}
