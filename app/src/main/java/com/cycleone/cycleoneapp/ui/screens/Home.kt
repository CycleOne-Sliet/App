package com.cycleone.cycleoneapp.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cycleone.cycleoneapp.R
import com.cycleone.cycleoneapp.services.NavProvider
import com.cycleone.cycleoneapp.services.StandLocation
import com.cycleone.cycleoneapp.services.getStandLocations
import com.cycleone.cycleoneapp.ui.components.LocationCard
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.runBlocking

class Home {

    val onViewAll = { NavProvider.controller.navigate("/allLocations") }

    @Composable
    fun Create(
        modifier: Modifier = Modifier,
        authInstance: FirebaseAuth? = FirebaseAuth.getInstance(),
        navController: NavController = NavProvider.controller
    ) {
        if (authInstance != null) {
            val user = authInstance.currentUser
            Log.d("User", user.toString())
            if (user == null) {
                navController.navigate("/landing")
            }
        }
        UI(modifier, suspend { getStandLocations() })
    }

    @Preview
    @Composable
    fun UI(
        modifier: Modifier = Modifier,
        getLocations: suspend () -> List<StandLocation> = {
            listOf(
                StandLocation(
                    location = "Test",
                    photoUrl = "youtube.com/favicon.ico"
                )
            )
        }
    ) {
        Column(
            modifier = modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
            ) {
                Image(
                    painter = painterResource(id = R.drawable.home_image),
                    "Home Image",
                    modifier = Modifier
                        .fillMaxWidth(0.9F)
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 40.dp),
                    contentScale = ContentScale.FillWidth
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Text("Available Stand Areas", style = MaterialTheme.typography.titleMedium)
                    TextButton(onClick = onViewAll) {
                        Text("View All")
                    }
                }
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    modifier = Modifier
                        .padding(start = 10.dp, top = 10.dp)
                        .fillMaxWidth(1f),
                ) {
                    items(runBlocking { getLocations() }) { location ->
                        LocationCard().Create(location)
                    }
                }
            }
        }
    }
}