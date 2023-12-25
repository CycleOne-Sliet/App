package com.cycleone.ttest2.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.cycleone.ttest2.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

const val REQUEST_ENABLE_BT = 1;

class Dashboard(private val controller: NavHostController, private val authInstance: FirebaseAuth) {
    @Composable
    fun StandAreas(modifier: Modifier = Modifier) {
        val user = authInstance.currentUser
        FirebaseDatabase.getInstance()
        if (user == null) {
            controller.navigate("/home")
        }
        val context = LocalContext.current
        Column(verticalArrangement = Arrangement.SpaceAround) {
            Image(
                painter = painterResource(id = R.drawable.stand),
                contentDescription = "cycle stand 1",
                modifier = Modifier
                    .requiredWidth(width = 380.dp)
                    .requiredHeight(height = 339.dp).align(Alignment.CenterHorizontally)
            )
            Button(
                onClick = {
                    controller.navigate("/journey")
                }, modifier = Modifier
                    .fillMaxWidth(0.9F).align(Alignment.CenterHorizontally)
            ) {
                Text("Scan QR Code")
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier
                    .fillMaxWidth(0.9F)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Available Stand Areas",
                    style = TextStyle(
                        fontSize = 18.sp
                    ),
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Text(
                    text = "View All",
                    color = Color.Black,
                    style = TextStyle(
                        fontSize = 12.sp
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .clickable(onClick = { Log.d("INFO", "No Other Locations for now") })
                )

            }

            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState()).align(Alignment.CenterHorizontally).fillMaxWidth(0.9F),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Location(name = "Computer Science Block", photoId = R.drawable.rectangle41)
                Location(
                    name = "Electronics and \n" +
                            "Communication Block", photoId = R.drawable.rectangle42
                )
            }
            Button(
                onClick = {
                    authInstance.signOut()
                    controller.navigate("/home")
                }, modifier = Modifier
                    .offset(y = 30.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Image(Icons.Outlined.Logout, "Logout")

            }

        }
    }

    @Composable
    fun Location(name: String, photoId: Int) {
        Button(onClick = {
            controller.navigate("/unlock")
        }, modifier = Modifier.clip(RoundedCornerShape(10.dp)), shape = RectangleShape) {
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp), modifier = Modifier
                    .requiredHeight(180.dp)
                    .requiredWidth(150.dp)
            ) {
                Image(
                    painter = painterResource(id = photoId),
                    contentDescription = name,
                    modifier = Modifier
                        .requiredWidth(width = 160.dp)
                        .requiredHeight(height = 120.dp)
                )
                Text(
                    text = name,
                    color = Color.White,
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier
                )
            }
        }
    }
}

@Preview(widthDp = 390, heightDp = 844)
@Composable
private fun StandAreasPreview() {
    Dashboard(rememberNavController(), FirebaseAuth.getInstance()).StandAreas(Modifier)
}