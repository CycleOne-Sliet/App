package com.cycleone.cycleoneapp.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.navigation.NavController
import com.cycleone.cycleoneapp.R
import com.cycleone.cycleoneapp.services.NavProvider
import com.google.firebase.auth.FirebaseAuth

class Landing {
    val onGetStarted = {
        NavProvider.controller.navigate("/sign_up")
    }
    val onContinue = {
        NavProvider.controller.navigate("/sign_in")
    }

    @Composable
    fun Create(modifier: Modifier = Modifier) {
        val navController: NavController = NavProvider.controller
        val user = FirebaseAuth.getInstance().currentUser
        Log.d("User", user.toString())
        if (user != null) {
            navController.navigate("/home")
        }
        UI(modifier)
    }

    @Composable
    @Preview
    fun UI(modifier: Modifier = Modifier) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .paint(
                    painterResource(id = R.drawable.landing_background_image),
                    contentScale = ContentScale.Crop
                ), verticalArrangement = Arrangement.Bottom
        ) {
            Log.d("ColorScheme", MaterialTheme.colorScheme.toString())
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.9F)
                    .background(
                        Brush.verticalGradient(
                            0.0f to Color.Transparent,
                            0.3f to Color(0.0f, 0.0f, 0.0f, 0.59f),
                            1.0f to Color.Black
                        )
                    )
                    .padding(top = 175.dp, start = 20.dp, end = 20.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    "CycleOne",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Column {
                    Text(
                        "\nConnect - Commute - Conserve",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Justify,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        "with us\n", style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
                Button(
                    onClick = { onGetStarted() },
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                ) {
                    Text(
                        "Get Started",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                }
                Column(verticalArrangement = Arrangement.SpaceAround) {
                    Text(
                        "\nAlready have an account?",
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    TextButton(
                        onClick = { onContinue() },
                        modifier = Modifier.padding(0.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            "Login Now",
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(0.dp),
                            color = MaterialTheme.colorScheme.secondary
                        )

                    }
                }
            }

        }
    }
}