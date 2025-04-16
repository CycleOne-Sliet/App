package com.cycleone.cycleoneapp.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.*

data class BikeHistoryItem(
    val id: Int,
    val startTime: Long,
    val endTime: Long,
    val startLocation: String,
    val endLocation: String,
    val distanceKm: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SimpleDateFormat")
@Composable
fun HistoryScreen(modifier: Modifier = Modifier, navController: NavController) {
    val historyItems = remember {
        mutableStateListOf(
            BikeHistoryItem(1, System.currentTimeMillis() - 9000000, System.currentTimeMillis() - 8700000, "Main Gate", "Library", 2.4),
            BikeHistoryItem(2, System.currentTimeMillis() - 17200000, System.currentTimeMillis() - 17100000, "Cafeteria", "Hostel", 1.8),
            BikeHistoryItem(3, System.currentTimeMillis() - 25900000, System.currentTimeMillis() - 25800000, "Gym", "Main Gate", 3.2)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ride History") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            if (historyItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.DirectionsBike,
                            contentDescription = "No History",
                            tint = Color.Gray,
                            modifier = Modifier.size(72.dp)
                        )
                        Text("No ride history found", color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(historyItems) { item ->
                        HistoryCard(item = item)
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryCard(item: BikeHistoryItem) {
    val durationMinutes = ((item.endTime - item.startTime) / 60000).toInt()
    val formattedStartTime = remember(item.startTime) {
        SimpleDateFormat("dd MMM yyyy, hh:mm a").format(Date(item.startTime))
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .background(Color(0xffff6b35))
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = formattedStartTime,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "From",
                            tint = Color.Green,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("From: ${item.startLocation}", fontSize = 14.sp, color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "To",
                            tint = Color.Red,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("To: ${item.endLocation}", fontSize = 14.sp, color = Color.White)
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("${item.distanceKm} km", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                    Text("$durationMinutes min", fontSize = 14.sp, color = Color.White)
                }
            }
        }
    }
}
