package com.cycleone.cycleoneapp.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinPage(modifier: Modifier = Modifier, navController: NavController = rememberNavController()) {
    val context = LocalContext.current
    val user = FirebaseAuth.getInstance().currentUser
    val uid = user?.uid

    var coins by remember { mutableStateOf<Int?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uid) {
        if (uid == null) {
            errorMessage = "User not logged in"
            isLoading = false
            return@LaunchedEffect
        }

        try {
            val doc = Firebase.firestore.collection("users").document(uid).get().await()
            val fetchedCoins = doc.getLong("Coins")?.toInt()
            if (fetchedCoins != null) {
                coins = fetchedCoins
                Log.d("Coinsdebug","$coins")
            } else {
                errorMessage = "Coins field missing"
            }
        } catch (e: Throwable) {
            errorMessage = "Failed to fetch coins: ${e.message}"
        } finally {
            isLoading = false
        }
    }
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "My Coins",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Available Coins",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (coins != null) {
                Box(
                    modifier = Modifier
                        .background(Color(0xFFFCD34D), RoundedCornerShape(20.dp))
                        .padding(horizontal = 40.dp, vertical = 20.dp)
                ) {
                    Text(
                        text = "$coins",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4B5563)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
/*
            Button(
                onClick = {}
                ,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
            ) {
                Text("Use Cycle (-5 Coins)", color = Color.White)
            }
*/
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Total Cycles Used: Not used",
                fontSize = 16.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

            /*Button(
                onClick = {
                  /*  coins = 100
                    cyclesUsed = 0
                    Toast.makeText(context, "Coins reset to 100", Toast.LENGTH_SHORT).show()
               */ },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
            ) {
                Text("Reset Coins", color = Color.White)
            }*/
        }
    }

