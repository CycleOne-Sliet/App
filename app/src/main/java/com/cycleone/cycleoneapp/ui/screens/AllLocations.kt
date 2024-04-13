package com.cycleone.cycleoneapp.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.cycleone.cycleoneapp.services.NavProvider
import com.cycleone.cycleoneapp.services.StandLocation
import com.cycleone.cycleoneapp.services.getStandLocations
import com.cycleone.cycleoneapp.ui.components.LocationCard
import com.google.firebase.auth.FirebaseAuth

class AllLocations {
    @Composable
    fun Create(navController: NavController = NavProvider.controller) {
        val user = FirebaseAuth.getInstance().currentUser
        Log.d("User", user.toString())
        if (user == null) {
            navController.navigate("/landing")
        }
        var standLocations: List<StandLocation> = listOf()
        LaunchedEffect(user) {
            standLocations = getStandLocations()
        }
        UI(navController, standLocations)
    }

    @Composable
    fun UI(navController: NavController = NavProvider.controller, locations: List<StandLocation>) {
        val topScrollable = ScrollableState { x -> x }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .scrollable(topScrollable, Orientation.Vertical),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TextButton(
                onClick = { navController.popBackStack() }, modifier = Modifier
                    .background(Color.Transparent)
                    .align(AbsoluteAlignment.Left)
            ) {
                Text("‹", fontSize = 50.sp, style = MaterialTheme.typography.titleLarge)
            }

            val verticalState = rememberScrollState(0)
            LazyColumn(
                modifier = Modifier
                    .scrollable(verticalState, Orientation.Vertical)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(
                    10.dp,
                    Alignment.CenterVertically
                ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(locations) { location ->
                    LocationCard().Create(location, navController)
                }
            }

        }
    }

    @Preview
    @Composable
    fun Preview() {
        UI(rememberNavController(), listOf(StandLocation("hfewo", "kjfr", listOf())))
    }
}