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
import androidx.navigation.compose.rememberNavController
import com.cycleone.cycleoneapp.R
import com.cycleone.cycleoneapp.services.NavProvider
import com.cycleone.cycleoneapp.services.getStandLocations
import com.cycleone.cycleoneapp.ui.components.LocationCard
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.runBlocking

class Home {
    @Composable
    fun Create(
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
        Column(
            modifier = Modifier
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
                        .padding(20.dp)
                ) {
                    Text("Available Stand Areas", style = MaterialTheme.typography.titleMedium)
                    TextButton(onClick = { navController.navigate("/allLocations") }) {
                        Text("View All")
                    }
                }
                LazyRow(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(0.9f),
                ) {
                    items(runBlocking { getStandLocations() }) { location ->
                        LocationCard().Create(location)
                    }
                }

                FirebaseAuth.getInstance().getAccessToken(true)
                    .addOnSuccessListener { t -> Log.d("Token", t.token.toString()) }
            }
        }
    }
}

@Preview
@Composable
fun HomePreview() {
    Home().Create(null, rememberNavController())
}