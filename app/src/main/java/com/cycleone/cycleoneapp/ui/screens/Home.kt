package com.cycleone.cycleoneapp.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.cycleone.cycleoneapp.R
import com.cycleone.cycleoneapp.services.NavProvider
import com.cycleone.cycleoneapp.services.getStandLocations
import com.cycleone.cycleoneapp.ui.components.MapDisplay
import com.cycleone.cycleoneapp.ui.theme.monsterratFamily
import com.google.firebase.auth.FirebaseAuth

class Home {

    val onViewAll = { NavProvider.controller.navigate("/allLocations") }

    @Composable
    fun Create(
        modifier: Modifier = Modifier,
        authInstance: FirebaseAuth? = FirebaseAuth.getInstance(),
        navController: NavController = NavProvider.controller
    ) {
        var username by remember {
            mutableStateOf("")
        }
        if (authInstance != null) {
            val user = authInstance.currentUser
            username = user?.displayName ?: ""
            Log.d("User", user.toString())
            if (user == null) {
                navController.navigate("/landing")
            }
        }
        UI(modifier, username)
    }

    @Preview
    @Composable
    fun UI(
        modifier: Modifier = Modifier,
        username: String = "Sandeep Kumar",
    ) {

        Column(
            modifier = modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
            ) {
                Button(enabled = false, onClick = { /*TODO*/ }) {
                    Image(
                        painter = painterResource(R.drawable.menu),
                        "Menu",
                        modifier = Modifier
                            .width(32.dp)
                            .height(32.dp)
                    )
                }
                Image(
                    painter = painterResource(R.drawable.logo_cycleone_new_1),
                    "Logo",
                    modifier = Modifier
                        .width(58.dp)
                        .height(37.dp)
                )
                Button(enabled = false, onClick = { /*TODO*/ }) {
                    Image(
                        painter = painterResource(R.drawable.vector_1_),
                        "Menu",
                        modifier = Modifier
                            .width(32.dp)
                            .height(32.dp)
                    )
                }
            }
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Hello !!!",
                        modifier = Modifier.padding(bottom = 5.dp),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = monsterratFamily,
                        color = Color.White
                    )
                    Text(
                        username,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Light,
                        fontFamily = monsterratFamily,
                        color = Color.White
                    )
                    Text(
                        "Your last session",
                        modifier = Modifier.padding(vertical = 15.dp),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = monsterratFamily,
                        color = Color.White
                    )
                }


                Column(
                    modifier = Modifier
                        .border(2.dp, Color.White, RoundedCornerShape(10.dp))
                        .background(Color(0xff252322)),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MapDisplay().Create(
                        modifier = Modifier
                            .padding(15.dp)
                            .fillMaxWidth()
                            .heightIn(min = 250.dp, max = 300.dp)
                            .border(2.dp, Color.White, RoundedCornerShape(5.dp))
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .padding(15.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                "Distance",
                                fontFamily = monsterratFamily,
                                fontSize = 20.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Light
                            )
                            Text(
                                "2.67Km",
                                color = Color(0xffff6b35),
                                fontFamily = monsterratFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 24.sp
                            )
                        }
                        Column {
                            Text(
                                "Time",
                                fontFamily = monsterratFamily,
                                fontSize = 20.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Light
                            )
                            Text(
                                "27 min",
                                color = Color(0xffff6b35),
                                fontFamily = monsterratFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 24.sp
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8F)
                    .height(90.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xff252322))
                        .border(3.dp, Color(0xffff6b35), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp),
                    Arrangement.SpaceBetween,
                    Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors()
                            .copy(containerColor = Color.Transparent)
                    ) {
                        Icon(Icons.Default.Notifications, "Notification")
                    }
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors()
                            .copy(containerColor = Color.Transparent)
                    ) {
                        Icon(Icons.Default.Phone, "Phone")
                    }
                }

                Button(
                    onClick = {NavProvider.controller.navigate("/unlock_screen")},
                    modifier = Modifier
                        .padding(bottom = 15.dp)
                        .fillMaxHeight()
                        .border(3.dp, Color(0xffff6b35), RoundedCornerShape(10.dp)),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors()
                        .copy(containerColor = Color(0xff252322))
                ) {
                    Image(
                        painter = painterResource(R.drawable.qr_code_1_),
                        "Scan",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .padding(vertical = 6.dp)
                            .width(56.dp)
                            .height(56.dp)
                    )
                }

            }
        }
    }
}