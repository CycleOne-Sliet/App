package com.cycleone.cycleoneapp.ui.screens


import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit


@SuppressLint("ContextCastToActivity")
@Composable
fun PhoneVerificationScreen(navController: NavController) {
    var phoneNumber by remember { mutableStateOf("") }
    var codeSent by remember { mutableStateOf(false) }
    var verificationId by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf("") }
    val activity = LocalContext.current as? Activity


    Column(modifier = Modifier.padding(16.dp).fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = {
                if (it.length <= 10 && it.all { char -> char.isDigit() }) {
                    phoneNumber = it
                } },
            label = { Text("Phone Number") },
            placeholder = { Text("Enter 10-digit number") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (!codeSent) {
            Button(onClick = {
                if (phoneNumber.length == 10) {
                    val fullNumber = "+91$phoneNumber"
                    activity?.let {
                        Log.d("TAG9", "ProfileScreen: $phoneNumber")
                        sendVerificationCode(it,fullNumber) { id ->
                            verificationId = id
                            Log.d("TAG7", "ProfileScreen: $verificationId")
                            codeSent = true
                            statusMessage = "OTP sent successfully"
                        }
                        Log.d("TAG8", "ProfileScreen: $phoneNumber")
                    } }
                else {
                    statusMessage = "Enter valid 10-digit number"
                    // Show error (e.g., via Toast or Snackbar)
                    Log.d("PhoneInput", "Invalid number")
                }

            }) {
                Text("Send OTP")
            }
        } else {
            OutlinedTextField(
                value = otp,
                onValueChange = { otp = it },
                label = { Text("Enter OTP") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            Button(onClick = {
                activity?.let {
                    verifyOTP(it,verificationId, otp,navController,
                        onSuccess = {
                            statusMessage = "Phone number verified successfully"
                        },
                        onFailure = { error ->
                            statusMessage = "Verification failed: $error"
                        }
                    )
                }}) {
                Text("Verify")
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
        StatusMessageBox(message = statusMessage, onTimeout = { statusMessage = "" })
    }

}


fun sendVerificationCode(
    activity: Activity,
    phone: String,
    onCodeSent: (String) -> Unit
) {


    val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
        .setPhoneNumber(phone)  // Phone number to send OTP
        .setTimeout(60L, TimeUnit.SECONDS)  // Set timeout duration
        .setActivity(activity)  // Provide the Activity context here
        .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // Automatically verify OTP if possible
                FirebaseAuth.getInstance().currentUser?.updatePhoneNumber(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.e("PhoneAuth2", "Verification failed: ${e.message}")
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                onCodeSent(verificationId)
                Log.d("PhoneAuth", "Code sent: $verificationId")// Callback with verification ID
            }
        })
        .build()

    PhoneAuthProvider.verifyPhoneNumber(options)
}



fun verifyOTP( activity: Activity,verificationId: String, otp: String,
               navController: NavController,
               onSuccess: () -> Unit,
               onFailure: (String) -> Unit) {
    val credential = PhoneAuthProvider.getCredential(verificationId, otp)
    val user = FirebaseAuth.getInstance().currentUser

    user?.updatePhoneNumber(credential)?.addOnSuccessListener {
        onSuccess()
        Log.d("PhoneAuth3", "Phone number verified and updated")
        navController.popBackStack()
        navController.navigate("/home")
        // You can also store it in Firestore if needed
    }?.addOnFailureListener {
        Log.e("PhoneAuth4", "Verification failed: ${it.message}")
        onFailure(it.message ?: "Verification failed")
    }
}



@Composable
fun StatusMessageBox(
    message: String,
    onTimeout: () -> Unit,
    durationMillis: Long = 3000L
) {
    if (message.isNotEmpty()) {
        LaunchedEffect(message) {
            delay(durationMillis)
            onTimeout()
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp)
                .background(Color(0xFF323232), shape = RoundedCornerShape(10.dp))
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = message,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}




