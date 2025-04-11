package com.cycleone.cycleoneapp.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class NotificationItem(
    val id: Int,
    val title: String,
    val message: String,
    val timestamp: Long,
    var isRead: Boolean = false
)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(modifier: Modifier = Modifier, navController: NavController) {
    val notifications = remember {
        mutableStateListOf(
            NotificationItem(1, "Welcome!", "Thanks for joining us!", System.currentTimeMillis() - 60000),
            NotificationItem(2, "Update Available", "Version 2.0 is now available.", System.currentTimeMillis() - 3600000),
            NotificationItem(3, "Reminder", "Don't forget to complete your profile.", System.currentTimeMillis() - 86400000)
        )
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications") },
                actions = {
                    if (notifications.isNotEmpty()) {
                        IconButton(onClick = {
                            notifications.clear()
                            scope.launch {
                                snackbarHostState.showSnackbar("All notifications cleared")
                            }
                        }) {
                            Icon(Icons.Default.ClearAll, contentDescription = "Clear All")
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (notifications.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "No notifications",
                        tint = Color.Gray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No notifications", color = Color.Gray, fontSize = 16.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    items(notifications, key = { it.id }) { notification ->
                        NotificationCard(
                            item = notification,
                            onClick = { notification.isRead = true },
                            onDelete = {
                                val removed = notification.copy()
                                notifications.remove(notification)
                                scope.launch {
                                    val result = snackbarHostState.showSnackbar(
                                        message = "Notification deleted",
                                        actionLabel = "Undo"
                                    )
                                    if (result == SnackbarResult.ActionPerformed) {
                                        notifications.add(removed)
                                    }
                                }
                            },
                            onToggleRead = {
                                notification.isRead = !notification.isRead
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationCard(
    item: NotificationItem,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onToggleRead: () -> Unit
) {
    val formattedTime = remember(item.timestamp) {
        SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(item.timestamp))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isRead) Color(0xFFF1F5F9) else Color(0xFFE0F2FE)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = item.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Text(
                    text = formattedTime,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.message,
                color = Color.DarkGray,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(onClick = onToggleRead) {
                    Text(if (item.isRead) "Mark as Unread" else "Mark as Read")
                }
                TextButton(onClick = onDelete) {
                    Text("Delete", color = Color.Red)
                }
            }
        }
    }
}
