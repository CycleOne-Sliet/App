package com.cycleone.cycleoneapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.cycleone.cycleoneapp.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

class SplashScreen {
    @Composable
    fun Create(navController: NavController) {
        val user = FirebaseAuth.getInstance().currentUser
        val nextLocation = if (user == null) {
            "/onboarding"
        } else {
            "/home"
        }
        LaunchedEffect(key1 = null) {
            delay(2000)
            navController.navigate(nextLocation)
        }
        UI()
    }

    @Preview
    @Composable
    fun UI() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF47216)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(32.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // App Name and Icon
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_cycleone_new_01),
                        contentDescription = "CycleOne Icon",
                        modifier = Modifier.size(120.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "CycleOne",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Sponsors
                Column(
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SponsorSlot("Sponsored by NHPC", R.drawable.nhpc_icon)
                    SponsorSlot("SLIET", R.drawable.sliet_logo)
                }

                // Dr. Amit Kansal – Face + Label
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.dr_amit_kansal_photo),
                        contentDescription = "Dr. Amit Kansal",
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.White, CircleShape)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Thanking Dr. Amit Kansal",
                        fontSize = 16.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    @Composable
    fun SponsorSlot(label: String, iconRes: Int) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = label,
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = label,
                fontSize = 16.sp,
                color = Color.White
            )
        }
    }
}