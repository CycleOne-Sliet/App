package com.cycleone.cycleoneapp.services

import android.app.Application
import android.util.Log
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.CameraController.COORDINATE_SYSTEM_VIEW_REFERENCED
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Responsible for handling qr codes
// Simply does a callback with the data in the Qr
// Silently closes otherwise
class QrCode : Application() {

    companion object {
        @Volatile
        var qrScanned = 0

        @OptIn(ExperimentalMaterial3Api::class)
        @Composable
        fun startCamera(
            onSuccess: suspend (String) -> Unit,
            lifecycleOwner: LifecycleOwner,
        ) {
            // AndroidView Necessary because Compose does not allow for streaming data from camera
            // and displaying it easily
            val coroutineScope = rememberCoroutineScope()
            var errorText: String? by remember {
                mutableStateOf(null)
            }
            if (errorText != null) {
                BasicAlertDialog(onDismissRequest = {
                    errorText = null
                }) {
                    Text("Error: $errorText")
                }
            }
            AndroidView(modifier = Modifier.fillMaxSize(0.75F), factory = { context ->
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
                        if ((barcodeResults == null) ||
                            (barcodeResults.size == 0) ||
                            (barcodeResults.first() == null)
                        ) {
                            return@MlKitAnalyzer
                        }
                        // Parse to String and do the callback
                        val result = barcodeResults[0].rawValue
                        result?.let {
                            Log.d("Scanned QR", it)
                            if (qrScanned == 0) {
                                qrScanned++
                                CoroutineScope(Dispatchers.Main).launch {
                                    NavProvider.showDebugModal()
                                    coroutineScope.launch(CoroutineExceptionHandler { _, throwable ->
                                        Log.e("FancyBtnExceptionMsg", throwable.message.toString())
                                        Log.e("FancyBtnExceptionCause", throwable.cause.toString())
                                        Log.e(
                                            "FancyBtnExceptionTrace",
                                            throwable.stackTraceToString()
                                        )
                                        errorText = throwable.message
                                    }) {
                                        try {
                                            onSuccess(it)
                                            delay(1000L)
                                            errorText = null
                                            qrScanned--
                                        } catch (throwable: Throwable) {
                                            errorText = throwable.message
                                            Log.e(
                                                "FancyBtnExceptionMsgCatch",
                                                throwable.message.toString()
                                            )
                                            Log.e(
                                                "FancyBtnExceptionCauseCatch",
                                                throwable.cause.toString()
                                            )
                                            Log.e(
                                                "FancyBtnExceptionTraceCatch",
                                                throwable.stackTraceToString()
                                            )
                                        }
                                    }
                                }
                            }
                        }
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
