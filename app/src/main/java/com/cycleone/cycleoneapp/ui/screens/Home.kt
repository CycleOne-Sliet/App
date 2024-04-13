package com.cycleone.cycleoneapp.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cycleone.cycleoneapp.R
import com.cycleone.cycleoneapp.services.NavProvider
import com.cycleone.cycleoneapp.services.getStandLocations
import com.cycleone.cycleoneapp.ui.components.LocationCard
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.runBlocking

class Home {
    @Composable
    @Preview
    fun Create() {
        val navController = NavProvider.controller
        val user = FirebaseAuth.getInstance().currentUser
        Log.d("User", user.toString())
        if (user == null) {
            navController.navigate("/landing")
        }
        val topScrollable = ScrollableState { x -> x }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .scrollable(topScrollable, Orientation.Vertical),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                TextButton(
                    onClick = { navController.popBackStack() }, modifier = Modifier
                        .background(Color.Transparent)
                        .align(AbsoluteAlignment.Left)
                ) {
                    Text("‹", fontSize = 50.sp, style = MaterialTheme.typography.titleLarge)
                }
                Image(
                    painter = painterResource(id = R.drawable.home_image),
                    "Home Image",
                    modifier = Modifier
                        .fillMaxWidth(0.9F)
                        .align(Alignment.CenterHorizontally),
                    contentScale = ContentScale.FillWidth
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text("Available Stand Areas", style = MaterialTheme.typography.titleMedium)
                    TextButton(onClick = { navController.navigate("/allLocations") }) {
                        Text("View All")
                    }
                }
                val horizontalState = rememberScrollState(0)
                LazyRow(
                    modifier = Modifier
                        .scrollable(horizontalState, Orientation.Horizontal)
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(
                        10.dp,
                        Alignment.CenterHorizontally
                    )
                ) {
                    items(runBlocking { getStandLocations() }) { location ->
                        LocationCard().Create(location)
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(onClick = {}) {
                    Icon(Icons.Default.Person, "Profile")
                }
                OutlinedButton(onClick = {
                    navController.navigate("/unlock_screen")
                }) {
                    Icon(Icons.Default.Search, "Scan")
                }
                OutlinedButton(onClick = {}) {
                    Icon(Icons.Default.Notifications, "Notifications")
                }
            }
        }
    }
}