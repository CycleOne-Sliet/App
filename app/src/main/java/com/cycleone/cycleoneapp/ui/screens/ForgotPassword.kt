package com.cycleone.cycleoneapp.ui.screens

import android.content.ActivityNotFoundException
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.cycleone.cycleoneapp.R
import com.cycleone.cycleoneapp.ui.components.PrestyledText
import com.google.firebase.auth.FirebaseAuth


class ForgotPassword {
    @Composable
    fun Create(modifier: Modifier) {
        val context = LocalContext.current
        UI(modifier = modifier, onForgotPassword = { email ->
            FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnSuccessListener {
                try {
                    val intent = Intent(Intent.ACTION_MAIN)
                    intent.addCategory(Intent.CATEGORY_APP_EMAIL)
                    startActivity(context, intent, null)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(
                        context,
                        "There is no email client installed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }.addOnFailureListener {
                Toast.makeText(
                    context,
                    "Unable to send password reset link",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("ForgotPassword", "Unable to send password reset mail", it)
            }
        })
    }

    @Composable
    @Preview
    fun UI(modifier: Modifier = Modifier, onForgotPassword: (String) -> Unit = {}) {
        val scrollState = rememberScrollState()
        var email by remember {
            mutableStateOf("")
        }
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                alpha = if (isSystemInDarkTheme()) {
                    0.0F
                } else {
                    1.0F
                }, painter = painterResource(id = R.drawable.locate), contentDescription = "Locate"
            )
            Text("Forgot Password?", style = MaterialTheme.typography.titleMedium)
            Text("Enter your email to reset password", style = MaterialTheme.typography.labelMedium)

            PrestyledText().Regular(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(),
                placeholder = "Enter your Email",
                onChange = { x -> email = x },
                label = "Mail",
                icon = Icons.Outlined.Email
            )

            Button(
                modifier = Modifier
                    .padding(horizontal = 55.dp, vertical = 10.dp)
                    .align(AbsoluteAlignment.Left),
                onClick = {
                    onForgotPassword(email)
                }) {
                Text(
                    "Forgot Password?",
                )

            }
        }
    }
}