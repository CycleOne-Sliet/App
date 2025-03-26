package com.cycleone.cycleoneapp.ui.screens

import android.util.Log
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.cycleone.cycleoneapp.services.StandLocation
import com.cycleone.cycleoneapp.services.getStandLocations
import com.cycleone.cycleoneapp.ui.components.LocationCard
import com.google.firebase.auth.FirebaseAuth

class AllLocations {
    @Composable
    fun Create(
        modifier: Modifier = Modifier,
        navController: NavController
    ) {
        val user = FirebaseAuth.getInstance().currentUser
        Log.d("User", user.toString())
        if (user == null) {
            navController.navigate("/landing")
        }
        var standLocations by remember {
            mutableStateOf(listOf<StandLocation>())
        }
        LaunchedEffect(user) {
            standLocations = getStandLocations()
            Log.d("Stand Locations", standLocations.toString())
        }
        UI(modifier, navController, standLocations)
    }

    @Preview
    @Composable
    fun UI(
        modifier: Modifier = Modifier,
        navController: NavController = rememberNavController(),
        locations: List<StandLocation> = listOf(StandLocation("hfewo", "kjfr"))
    ) {
        val topScrollable = ScrollableState { x -> x }
        Column(
            modifier = modifier
                .fillMaxSize()
                .scrollable(topScrollable, Orientation.Vertical),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
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

}