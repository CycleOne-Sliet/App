package com.cycleone.cycleoneapp.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.cycleone.cycleoneapp.R
import com.cycleone.cycleoneapp.services.NavProvider
import com.google.firebase.auth.FirebaseAuth

class Landing {
    @Composable
    @Preview
    public fun Create() {
        val navController = NavProvider.controller
        val user = FirebaseAuth.getInstance().currentUser
        Log.d("User", user.toString())
        if (user != null) {
            navController.navigate("/home")
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .paint(
                    painterResource(id = R.drawable.landing_background_image),
                    contentScale = ContentScale.Crop
                ), verticalArrangement = Arrangement.Bottom
        ) {
            val bgColor = MaterialTheme.colorScheme.background
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.45F)
                    .background(
                        Brush.verticalGradient(
                            0.0f to Color.Transparent,
                            0.25f to Color(bgColor.red, bgColor.green, bgColor.blue, 0.3f),
                            1.0f to MaterialTheme.colorScheme.background
                        )
                    )
                    .padding(top = 175.dp, start = 20.dp, end = 20.dp), verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text("CycleOne", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                Column() {
                    Text(
                        "Connect - Commute - Conserve",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text("with us", style = MaterialTheme.typography.bodyMedium,

                        color = MaterialTheme.colorScheme.onBackground
                        )
                }
                Button(
                    onClick = { navController.navigate("/sign_up")},
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(15.dp)
                ) {
                    Text("Get Started", style = MaterialTheme.typography.bodyLarge,

                        )
                }
                Column() {
                    Text(
                        "Already have an account?",
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    TextButton(onClick = { navController.navigate("/sign_in") }) {
                        Text(
                            "Login Now",
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),

                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }

        }
    }
}