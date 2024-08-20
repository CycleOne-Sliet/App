package com.cycleone.cycleoneapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cycleone.cycleoneapp.R
import com.cycleone.cycleoneapp.services.NavProvider
import com.cycleone.cycleoneapp.ui.components.PrestyledText

class ForgotPassword {
    @Composable
    @Preview
    fun Create(modifier: Modifier = Modifier) {
        val navController = NavProvider.controller
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextButton(
                onClick = { navController.popBackStack() }, modifier = Modifier
                    .background(Color.Transparent)
                    .align(AbsoluteAlignment.Left)
            ) {
                Text("‹", fontSize = 50.sp, style = MaterialTheme.typography.titleLarge)
            }
            Image(painter = painterResource(id = R.drawable.forgot_password_image), "Locate")
            Text("Forgot your password?", style = MaterialTheme.typography.titleLarge)
            Text(
                "Enter your email address and we will mail you an otp reset link to reset your otp.",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.fillMaxWidth(0.7F),
                textAlign = TextAlign.Center
            )
            PrestyledText().Regular(
                placeholder = "Enter your Email",
                onChange = {},
                label = "Mail",
                icon = Icons.Default.Email
            )
            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth(0.75F)
                    .padding(vertical = 10.dp),
                shape = RoundedCornerShape(15.dp)

            ) {
                Text("Next ›", style = MaterialTheme.typography.titleMedium)
            }
            Text("Remember Password?, Log In", modifier = Modifier.padding(vertical = 5.dp))
        }
    }
}