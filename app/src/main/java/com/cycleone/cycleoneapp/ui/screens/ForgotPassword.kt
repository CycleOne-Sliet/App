package com.cycleone.cycleoneapp.ui.screens

import android.content.ActivityNotFoundException
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import com.cycleone.cycleoneapp.R
import com.cycleone.cycleoneapp.services.NavProvider
import com.cycleone.cycleoneapp.ui.components.FancyButton
import com.cycleone.cycleoneapp.ui.components.PrestyledText
import com.cycleone.cycleoneapp.ui.theme.monsterratFamily
import com.google.firebase.auth.FirebaseAuth


class ForgotPassword {
    @Composable
    fun Create(modifier: Modifier) {
        val context = LocalContext.current
        UI(modifier = modifier, onForgotPassword = { email ->
            FirebaseAuth.getInstance().sendPasswordResetEmail(
                email,
            ).addOnSuccessListener {
                try {
                    val intent = Intent(Intent.ACTION_MAIN)
                    intent.addCategory(Intent.CATEGORY_APP_EMAIL)
                    startActivity(context, intent, null)
                    Toast.makeText(
                        context,
                        "Reset link sent to mail",
                        Toast.LENGTH_SHORT
                    ).show()
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
                .padding(vertical = 60.dp)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Image(
                modifier = Modifier
                    .width(214.dp)
                    .height(214.dp),
                painter = painterResource(id = R.drawable.forgot_password),
                contentDescription = "Locate"
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Forgot Password?", fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = monsterratFamily,
                    color = Color.White
                )
                Text(
                    "Enter your email address and we will send \n" +
                            "a password reset link to it.",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = monsterratFamily,
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
                PrestyledText().Regular(
                    modifier = Modifier
                        .padding(top = 40.dp)
                        .fillMaxWidth(),
                    placeholder = "Enter your Email",
                    onChange = { x -> email = x },
                    icon = Icons.Outlined.Email
                )
            }


            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                FancyButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Send Link",
                    onClick = {
                        onForgotPassword(email)
                    })
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "New Member?",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = monsterratFamily,
                        color = Color.White
                    )
                    TextButton(onClick = { NavProvider.controller.navigate("/sign_up") }) {
                        Text("Sign Up Now", color = Color(0xffff6b35))
                    }
                }
            }
        }
    }
}